package com.example.product_sale_app.ui.chat
import okhttp3.Interceptor
import okhttp3.Response
private const val TEST_JWT =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxNiIsInVuaXF1ZV9uYW1lIjoiY3VzdG9tZXIiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJDdXN0b21lciIsImVtYWlsIjoiY3VzdG9tZXJAY3VzdG9tZXIuY29tIiwianRpIjoiNmYwNmY3NDAtZDllYS00NGRjLWI3N2ItODA3YjRlODQ3YjEyIiwibmJmIjoxNzQ5NjU5NTI1LCJleHAiOjE3NDk2NjMxMjUsImlzcyI6IlBSTTM5MiIsImF1ZCI6IlBSTTM5MiJ9.U1a1Ilyndso8RncdP4YnoSnv_2DvG1hiPIF62Gga9OM"

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val orig = chain.request()
        val req = orig.newBuilder()
            .addHeader("Authorization", "Bearer $TEST_JWT")
            .build()
        return chain.proceed(req)

    }
}
