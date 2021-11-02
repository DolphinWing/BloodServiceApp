package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import dolphin.android.apps.BloodServiceApp.R

class BloodCenter(context: Context) {
    companion object {
        const val URL_BASE_BLOOD_ORG = "http://www.blood.org.tw"
        const val URL_BLOOD_STORAGE = "$URL_BASE_BLOOD_ORG/Internet/main/index.aspx"
        const val URL_LOCAL_BLOOD_CENTER_WEEK =
            URL_BASE_BLOOD_ORG + "/Internet/mobile/docs/local_blood_center_week.aspx" +
                "?site_id={site}&date={date}" // yyyy/MM/dd

        const val QS_LOCATION_MAP_CITY = /* blood_center_donate_station + */"&cityID={city}"
        const val URL_LOCAL_BLOOD_LOCATION_MAP =
            URL_BASE_BLOOD_ORG + "/Internet/mobile/docs/local_blood_center_map.aspx" +
                "?site_id={site}&select_city={city}&spotID={spot}"
    }

    private val names = context.resources.getStringArray(R.array.blood_center)
    private val ids = context.resources.getIntArray(R.array.blood_center_id)
    private val fbs = context.resources.getStringArray(R.array.blood_center_facebook)
    private val stations = context.resources.getStringArray(R.array.blood_center_donate_station)
    private val cities =
        context.resources.getStringArray(R.array.blood_center_donate_station_city)
    private val cityIds =
        context.resources.getStringArray(R.array.blood_center_donate_station_city_id)

    open class Center(
        val name: String = "",
        val id: Int = 0,
        val facebook: String = "",
        private val stations: String = "",
        val cities: String = "",
        private val cityIds: String = "",
    ) {
        constructor(index: Int, info: BloodCenter) : this(
            info.names[index],
            info.ids[index],
            info.fbs[index],
            info.stations[index],
            info.cities[index],
            info.cityIds[index],
        )

        fun city(): List<Int> {
            val list = ArrayList<Int>()
            cityIds.split(",").forEach { id -> list.add(id.toInt()) }
            return list
        }
    }

    fun main(): Center = Center(0, this)

    private fun taipei(): Center = Center(1, this)

    private fun hsinchu(): Center = Center(2, this)

    private fun taichung(): Center = Center(3, this)

    private fun tainan(): Center = Center(4, this)

    private fun kaohsiung(): Center = Center(5, this)

    fun values(): List<Center> = arrayListOf(
        taipei(), hsinchu(), taichung(), tainan(), kaohsiung(),
    )

    fun find(id: Int): Center = values().find { c -> id == c.id } ?: main()
}
