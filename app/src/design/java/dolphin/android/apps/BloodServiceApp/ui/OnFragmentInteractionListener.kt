/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 *
 *
 * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
 */
package dolphin.android.apps.BloodServiceApp.ui

/**
 * Created by dolphin on 2014/10/15.
 * Old MainActivity will use these methods.
 */
internal interface OnFragmentInteractionListener {
    fun onFragmentInteraction(id: String)

    fun onUpdateStart(id: String)

    fun onUpdateComplete(id: String)
}
