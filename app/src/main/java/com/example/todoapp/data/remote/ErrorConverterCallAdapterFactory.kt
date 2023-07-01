package com.example.todoapp.data.remote

import okhttp3.Request
import okio.Timeout
import retrofit2.*
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ErrorConverterCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val responseType = getParameterUpperBound(0, returnType as ParameterizedType)

        return object : CallAdapter<Any, Call<Any>> {
            override fun responseType(): Type {
                return responseType
            }

            override fun adapt(call: Call<Any>): Call<Any> {
                return ErrorHandlingCall(call)
            }
        }
    }

    private inner class ErrorHandlingCall<T>(private val delegate: Call<T>) : Call<T> {
        override fun enqueue(callback: Callback<T>) {
            delegate.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful && response.code() in 200..299) {
                        callback.onResponse(call, response)
                    } else {
                        callback.onFailure(call, HttpException(response))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onFailure(call, convert(t))
                }
            })
        }

        override fun isExecuted(): Boolean {
            return delegate.isExecuted
        }

        override fun clone(): Call<T> {
            return ErrorHandlingCall(delegate.clone())
        }

        override fun isCanceled(): Boolean {
            return delegate.isCanceled
        }

        override fun cancel() {
            delegate.cancel()
        }

        override fun execute(): Response<T> {
            return delegate.execute()
        }

        override fun request(): Request {
            return delegate.request()
        }

        override fun timeout(): Timeout {
            return delegate.timeout()
        }
    }

    private fun convert(throwable: Throwable): Throwable {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> NotSynchronizedDataException()
                    401 -> UnauthorizedException()
                    404 -> ElementNotFoundException()
                    500 -> ServerException()
                    else -> NetworkException(throwable)
                }
            }
            is IOException -> NetworkException(throwable)
            else -> Exception()
        }
    }

    class NotSynchronizedDataException : Exception("Not Synchronized Data")
    class UnauthorizedException: Exception("Unauthorized")
    class ElementNotFoundException: Exception("Not Found")
    class ServerException: Exception("Server Error")
    class NetworkException(cause: Throwable) : IOException("Network Error", cause)
}