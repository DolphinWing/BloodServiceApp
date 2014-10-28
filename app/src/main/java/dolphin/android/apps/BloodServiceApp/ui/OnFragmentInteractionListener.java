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
 */
public interface OnFragmentInteractionListener {
    public void onFragmentInteraction(String id);

    public void onUpdateStart(String id);

    public void onUpdateComplete(String id);
}
