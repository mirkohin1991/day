package de.smbsolutions.day.presentation.views;


import de.smbsolutions.day.functions.objects.Route;
import android.graphics.drawable.Drawable;
import android.widget.BaseAdapter;


public class MainListElement {
	
	private Drawable icon;
	private String textName;
	private String textDate;
	private Route route;

	

	
	private BaseAdapter adapter;
	
	
	public MainListElement( Route route) {
		super();
		this.route = route;
		
		textName = route.getRouteName();
		textDate = route.getDate();

	}
	

	
	//observers can call to register for data change notifications:
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}




	public String getTextName() {
		return textName;
	}
	


	public String getTextDate() {
		return textDate;
	}
	
	
	
	public Drawable getIcon() {
		return icon;
	}



	public Route getRoute() {
		return route;
	}
	
	


	
//	// handle click events
//	public  View.OnClickListener getOnItemClickListener() {
//		
//		
//		 return new View.OnClickListener() {
//	          
//				@Override
//				public void onClick(View view) {
//					// TODO Auto-generated method stub
//					
//				 }	
//					
//	         
//	        };
//		
//	        
//	        
//	        
//	        
//	      
//	        
//	       
//		
//	}
//	
//
//	
//	public View.OnLongClickListener getonLongClickListener() {
//		
//		return new ListView.OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		};
//	}
//	
	

	

}
