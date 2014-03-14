	package de.smbsolutions.day.presentation.dialogs;

	import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.presentation.fragments.MainFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

	public class StopRouteDialog  extends DialogFragment {
		
		private Route route;
		private Bundle bundle;
		private MainCallback mCallback;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			bundle = this.getArguments();
route = (Route) bundle.getParcelable("route");		

			return new AlertDialog.Builder(getActivity()).setTitle("Route anhalten")
					.setMessage("M�chten Sie die Route wirklich beenden?")
					.setNegativeButton(android.R.string.no, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// do nothing (will close dialog)
						}
					})
					.setPositiveButton(android.R.string.yes, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// The last route is always the active one
								route.closeRoute();
							dismiss();
							//Call the communication interface to start the follow-on fragment
							if (mCallback.getlastFragment() instanceof MainFragment){
								mCallback.onRouteStopped();
							}
					
						}
					}).create();
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);

			try {
				mCallback = (MainCallback) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString()
						+ " must implement OnButtonClick Interface");
			}
		}
	}
