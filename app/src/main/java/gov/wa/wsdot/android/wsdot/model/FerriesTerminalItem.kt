package gov.wa.wsdot.android.wsdot.model

/**
 *
 * @param departingTerminalID  Unique identifier for departing terminal
 * @param departingTerminalName  The name of the terminal
 * @param latitude  The latitude of the terminal
 * @param longitude  The longitude of the terminal
 */
class FerriesTerminalItem (
    var departingTerminalID: Int,
    var departingTerminalName: String,
    var latitude: Double,
    var longitude: Double
)