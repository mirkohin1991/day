


	package de.smbsolutions.day.presentation.dialogs;

	import de.smbsolutions.day.functions.interfaces.MainCallback;
	import de.smbsolutions.day.functions.objects.RouteList;
	import android.app.Activity;
	import android.app.AlertDialog;
	import android.app.Dialog;
	import android.content.DialogInterface;
	import android.content.DialogInterface.OnClickListener;
	import android.os.Bundle;
	import android.support.v4.app.DialogFragment;

	public class StopRouteDialog  extends DialogFragment {

		private RouteList routeList;
		private Bundle bundle;

		private MainCallback mCallback;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			bundle = this.getArguments();
			routeList = (RouteList) bundle.getSerializable("routeList");
		

			return new AlertDialog.Builder(getActivity()).setTitle("Route beenden")
					.setMessage("Möchten Sie die Route wirklich beenden?")
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
							routeList.getlastRoute().closeRoute();
							
							//Call the communication interface to start the follow-on fragment
							mCallback.onStopRoute();
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