/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
package dolphin.android.apps.BloodServiceApp.ui;

/**
 * Created by dolphin on 2014/10/15.
 * Old MainActivity will use these methods.
 */
interface OnFragmentInteractionListener {
    void onFragmentInteraction(String id);

    void onUpdateStart(String id);

    void onUpdateComplete(String id);
}
