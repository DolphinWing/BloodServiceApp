package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import dolphin.android.apps.BloodServiceApp.R

class BloodCenter(context: Context) {
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