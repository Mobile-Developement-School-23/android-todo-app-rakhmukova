package com.example.todoapp.data.remote.exceptions

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Custom CallAdapter.Factory implementation that wraps Retrofit's Call with an ErrorHandlingCall
 * to handle API exceptions.
 */
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
                    if (response.isSuccessful && response.code()
                        in HttpStatusCodes.SUCCESS_RANGE_START..HttpStatusCodes.SUCCESS_RANGE_END
                    ) {
                        callback.onResponse(call, response)
                    } else {
                        callback.onFailure(call, ExceptionConverter.toApiException(HttpException(response)))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onFailure(call, ExceptionConverter.toApiException(t))
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
}
