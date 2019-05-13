package gov.wa.wsdot.android.wsdot.api

import retrofit2.Call
import retrofit2.http.GET

// https://data.wsdot.wa.gov/mobile/WSFRouteSchedules.js

interface Webservice {

    /**
     * @GET declares an HTTP GET request
     * @Path("user") annotation on the userId parameter marks it as a
     * replacement for the {user} placeholder in the @GET path
     */
    @GET("/mobile/WSFRouteSchedules.js")
    fun getFerrySchedules(): Call<String>
}