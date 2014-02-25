package de.smbsolutions.day.presentation.views;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;


public class MainListAdapter extends ArrayAdapter<MainListElement> {
	
    private List<MainListElement> listElements;
    private Context context;
	private MainCallback mCallback;
   


    public MainListAdapter(Context context, int resourceId, 
                             List<MainListElement> listElements, MainCallback mCallback ) {
        super(context, resourceId, listElements);
        this.listElements = listElements;
        this.context = context;
        
        //Save the interface to communication with other fragments
        //Necessary for button click event
        this.mCallback = mCallback;
        
        // Set up as an observer for list item changes to
        // refresh the view.
        for (int i = 0; i < listElements.size(); i++) {
            listElements.get(i).setAdapter(this);
        }
    }

    
    
    // This method is called for each list element in order to build the view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        
        //View is only null when the List is created the first time
        if (view == null ) {
            LayoutInflater inflater =
                    (LayoutInflater) 
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view = inflater.inflate(R.layout.list_item, null);
           
        }
        
        
        MainListElement listElement = listElements.get(position);
        if (listElement != null) {
        	
//        	
//            view.setOnClickListener(listElement.getOnItemClickListener());
//             view.setOnLongClickListener(listElement.getonLongClickListener());
           Button buttonDetails = (Button) view.findViewById(R.id.imagebuttonDetails);
            TextView textName = (TextView) view.findViewById(R.id.textRouteName);
            final TextView textDate = (TextView) view.findViewById(R.id.textRouteDate);
//           if (icon != null) {
//                icon.setImageDrawable(listElement.getIcon());
//            }
            if (textName != null) {
                textName.setText(listElement.getTextName());
            }
            if (textDate != null) {
                textDate.setText(listElement.getTextDate());
            }
            
            if (buttonDetails != null) {
            	
            	//Tags are used to relocate the object later on during the event
            	buttonDetails.setTag(position);
            }
            
//            view.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					text2.setTextColor(Color.WHITE);
//					
//				}
//			});
            
            
            
            
            
            buttonDetails.setOnClickListener(new View.OnClickListener() 
            { 
                @Override
                public void onClick(View v) 
                {
                    
                	//Stored position of the button
                	int position = (Integer) v.getTag();
                	
                	//Getting the route object of the related row
                	//Transfering it to the interface in order to call the detailed map view
                	mCallback.onShowRoute(listElements.get(position).getRoute());
                
                }

            });
            
            

            
          
        }
        return view;
    }



	public List<MainListElement> getListElements() {
		return listElements;
	}
    

    
    

}
