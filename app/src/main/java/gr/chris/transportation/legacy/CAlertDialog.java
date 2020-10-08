package gr.chris.transportation.legacy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

import gr.chris.transportation.R;
import gr.chris.transportation.RouteDrawing;
import gr.chris.transportation.SQLEngine;

public class CAlertDialog extends Activity
{	
	public static int ACTION_EXIT_APP=100;
	ArrayList<String> tos=new ArrayList<String>();
	private static AlertDialog alertDialog;
	public final static int CHOICE_OK=23;
	public final  static int CHOICE_CANCEL=32;
	public static int ACTION_HIDE_DIALOG=200;
	Activity Ma;
	private static String cancel_button="OK";
	CAlertDialog(final Activity context,String title,String message,final String button,boolean cancel,final String cabum,final ChoseListener cs){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		try{
			Ma=context;
		}catch(Exception e){}
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder .setMessage(message) .setCancelable(cancel) .setPositiveButton(button,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {

					cs.onChose(CHOICE_OK);
					cs.onChoseAffirmative();
					if(Ma!=null){

					}
				}
			});
		if(cancel){
			alertDialogBuilder.setNegativeButton(cabum,new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int id){
						cs.onChose(CHOICE_CANCEL);
						cs.onChoseNegative();
						if(Ma!=null){

						}
					}
				});
		}

		alertDialog = alertDialogBuilder.create();

		/*Ma.listener.setResultListener(new MainActivity.ResultListener(){

		 @Override
		 public void onResult(ArrayList<String> results)
		 {

		 for(String s:results){
		 if(s.contains(button.toLowerCase())){
		 alertDialog.dismiss();
		 cs.onChose(CHOICE_OK);
		 cs.onChoseAffirmative();
		 break;
		 }
		 if(s.contains(cabum.toLowerCase())){
		 alertDialog.dismiss();
		 cs.onChose(CHOICE_CANCEL);
		 cs.onChoseNegative();
		 break;
		 }

		 }
		 }


		 });
		 */
		alertDialog.show();


	}
	CAlertDialog(final Activity context,String title,String message,final String button,boolean cancel,final int action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		//final MainActivity Ma=(MainActivity)context;
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message).setCancelable(cancel).setPositiveButton(button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				switch (action) {
					case 100:
						context.finish();

						break;
					case 200:
						dialog.cancel();

						break;

				}
			}
		});
		if (cancel) {
			alertDialogBuilder.setNegativeButton(cancel_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		}


		final AlertDialog alertDialog = alertDialogBuilder.create();

		/*Ma.listener.setResultListener(new MainActivity.ResultListener(){

		 @Override
		 public void onResult(ArrayList<String> results)
		 {

		 for(String s:results){
		 if(s.contains(button.toLowerCase())){
		 switch(action){
		 case 100:
		 context.finish();
		 break;
		 case 200:
		 alertDialog.cancel();
		 break;

		 }

		 break;
		 }
		 if(s.contains(cancel_button.toLowerCase())){

		 alertDialog.cancel();
		 break;
		 }

		 }
		 }


		 });*/
		alertDialog.show();


	}



	public CAlertDialog(Activity a, String title, final ArrayList<Marker> list, final GoogleMap map){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
		//final MainActivity Ma=(MainActivity)context;
		alertDialogBuilder.setTitle(title);
		ArrayList<String> markNames=new ArrayList<String>();
		for(Marker m:list){
			markNames.add(m.getTitle());
		}
		ArrayAdapter<String> ada=new ArrayAdapter<String>(a, android.R.layout.simple_list_item_1,markNames);
		alertDialogBuilder.setAdapter(ada, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {

				map.animateCamera(CameraUpdateFactory.newLatLngZoom(list.get(i).getPosition(),18.0f));
				dialog.dismiss();
			}
		});
		AlertDialog ad=alertDialogBuilder.create();
		ad.show();

	}



	public CAlertDialog(final Activity context, String title, String message, final Engine.Station s, final ArrayList<Engine.Route> ar, final String button, boolean cancel, final SQLEngine ss){


	}



	public CAlertDialog(final Activity context, String title, String message, final Engine.Station s, final ArrayList<Engine.Route> ar, final String button, boolean cancel, final SQLEngine ss,final Result handlerr){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		//final MainActivity Ma=(MainActivity)context;
		alertDialogBuilder.setTitle(title);

		final View mai=View.inflate(context, R.layout.alertlist,null);
		ListView sv=(ListView)mai.findViewById(R.id.alertlistListView);
		//WebView wb=(WebView)mai.findViewById(R.id.alertlistWebView);
		//wb.getSettings().setJavaScriptEnabled(true);
		//wb.loadUrl("https://www.google.gr/maps/place/"+s.latitude+",+"+s.longtitude+"/data=!3m1!1e3");
		ArrayList<String> arr=new ArrayList<String>();
		if(ar==null){
			new CAlertDialog(context,title,"Δεν βρέθηκαν δρομολόγια","ΟΚ",false,ACTION_HIDE_DIALOG);
			return;
		}
		for(Engine.Route r:ar){
			arr.add(r.busId+":"+r.routeName);
		}
		sv.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,arr));
		sv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
					Engine.Route r=ar.get(p3);
					handlerr.selected(r.routeId,true);

				}

			
		});
		sv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Engine.Route r=ar.get(position);
				String info="Πάροχος:"+r.AgencyName;
				info+="\nΔρομολόγιο:\n"+r.routeName;
				info+="\nID:"+r.routeId;
				new CAlertDialog(context,r.busId,info,"OK",false,ACTION_HIDE_DIALOG);
				return true;
			}
		});
		//TODO:Temporarly disabled. Change to use MYSQL instead of CSV files
		/*sv.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
					ArrayList<Integer> r=ss.routes[ar.get(p3).index];
					int orig=0,ind=0;
					final ArrayList<Engine.Station> Starr=new ArrayList<Engine.Station>();
					for(int i:r){
						for(Engine.Station st:ss.stations){
							if(st==null){
								continue;
							}
							
							if(st.stationId==i){
								Starr.add(st);
								if(s==st){
									orig=ind;
								}
							}
							ind++;
						}
					}
					//new CAlertDialog(context,ar.get(p3).busId,info,"OK",false,ACTION_HIDE_DIALOG);
					
					AlertDialog.Builder routeAlertDialogBuilder = new AlertDialog.Builder(context);
					//final MainActivity Ma=(MainActivity)context;
					routeAlertDialogBuilder.setTitle(ar.get(p3).busId+" Δρομολόγιο:");
					final View routeView=View.inflate(context,R.layout.statlist,null);
					ListView sv2=(ListView)routeView.findViewById(R.id.statlistsv);
					
					for(Engine.Station s: Starr){
						//tos.add(s.name+"-"+ss.municipalities[s.municipalityId].name);
						tos.add(s.name);
						
					}
					sv2.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,tos));
					sv2.setOnItemClickListener(new OnItemClickListener(){
		
							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
								
								new CAlertDialog(context,tos.get(p3),"",Starr.get(p3),ss.func(String.valueOf(Starr.get(p3).stationId)),"OK",false,ss);
								
							}

					});
					
					sv2.setOnItemLongClickListener(new OnItemLongClickListener(){

							@Override
							public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4){
								Engine.Station s=Starr.get(p3);
								String inf=s.fullDescription();
								new CAlertDialog(context,s.name,inf,"OK",false,CAlertDialog.ACTION_HIDE_DIALOG);
								return true;
							}


						});
					TextView routeNametv=(TextView)routeView.findViewById(R.id.routeName);
					routeNametv.setText(ar.get(p3).routeName);
					routeAlertDialogBuilder.setView(routeView);
					sv2.setSelection(orig);
					if(true){
						routeAlertDialogBuilder.setNegativeButton(cancel_button,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog,int id){
									dialog.cancel();
								}
							});
					}

					AlertDialog alertDialog2 = routeAlertDialogBuilder.create();
					alertDialog2.show();
					
					
				}
				
			
		});*/
		
		alertDialogBuilder.setView(mai);
		if(true){
			alertDialogBuilder.setNegativeButton(cancel_button,new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int id){
						dialog.cancel();
					}
				});
		}

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		
	}


	public ArrayList<Engine.Route> show;
	public HashMap<String,RouteDrawing> hashMp;
	public AlertDialog current=null;

	public CAlertDialog(final Activity context, final Engine.Station s, final ArrayList<Engine.Route> ar, final String button, boolean cancel, HashMap<String,RouteDrawing> hs, final Result res){
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		//final MainActivity Ma=(MainActivity)context;
		hashMp=hs;
		alertDialogBuilder.setTitle(context.getString(R.string.routes));
		show=new ArrayList<Engine.Route>(ar);
		final View mai=View.inflate(context, R.layout.route_selector,null);
		final ListView sv=(ListView)mai.findViewById(R.id.allRoutes);
		EditText ed=mai.findViewById(R.id.search_bar);
		ed.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				show=new ArrayList<Engine.Route>();
				String key=s.toString();
				for(Engine.Route r:ar){
					if(r.routeName.contains(key)||r.busId.contains(key)){
						show.add(r);
					}
				}
				ArrayList<String> arr=new ArrayList<String>();
				for(Engine.Route r:show){
					arr.add(r.busId+":"+r.routeName);
				}
				RouteCheck rc=new RouteCheck(context,show,res);
				if(show.size()==0){
					sv.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,new String[]{context.getString(R.string.notfound)}));
				}else {
					sv.setAdapter(rc);
				}
			}
		});
		//WebView wb=(WebView)mai.findViewById(R.id.alertlistWebView);
		//wb.getSettings().setJavaScriptEnabled(true);
		//wb.loadUrl("https://www.google.gr/maps/place/"+s.latitude+",+"+s.longtitude+"/data=!3m1!1e3");
		ArrayList<String> arr=new ArrayList<String>();
		for(Engine.Route r:ar){
			arr.add(r.busId+":"+r.routeName);
		}
		//sv.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,arr));
		RouteCheck rc=new RouteCheck(context,show,res);
		sv.setAdapter(rc);
		/*sv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
				res.selected(show.get(p3).routeId);
				current.dismiss();
			}
		});*/


		alertDialogBuilder.setView(mai);
		if(true){
			alertDialogBuilder.setNegativeButton(cancel_button,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog,int id){
					dialog.cancel();
				}
			});
		}

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		current=alertDialog;

	}


	public static abstract class Result{
		public abstract void selected(String routeID,boolean addOrRemove);
	}

	public static class ChoseListener{
		public AlertDialog dialog=alertDialog;
		public void onChose(int choice){

		}
		public void onChoseAffirmative(){

		}
		public void onChoseNegative(){

		}

	}


	public class RouteCheck implements ListAdapter{
		ArrayList<Engine.Route> arr;
		Context ref;
		Result inter;
		public RouteCheck(Context c,ArrayList<Engine.Route> a,Result r){
			arr=a;
			ref=c;
			inter=r;
		}
		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {

		}

		@Override
		public int getCount() {
			return arr.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi=View.inflate(ref,R.layout.bus_check_item,null);
			TextView tv=(TextView)vi.findViewById(R.id.item);
			final Engine.Route r=arr.get(position);
			tv.setText(r.busId+":"+r.routeName);
			CheckBox cb=(CheckBox)vi.findViewById(R.id.checkmark);
			cb.setChecked(hashMp.containsKey(r.routeId));
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					r.checked=isChecked;
					inter.selected(r.routeId,r.checked);
			   }
		   }
			);
			return vi;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return arr.size();
		}

		@Override
		public boolean isEmpty() {
			return arr.isEmpty();
		}
	}

	public class CListener implements OnClickListener,OnLongClickListener{

		@Override
		public boolean onLongClick(View p1){
			// TODO: Implement this method
			defaultAction();
			return false;
		}
		Context con;
		String text;

		@Override
		public void onClick(View p1){
			defaultAction();
		}

		public void defaultAction(){
			Toast.makeText(con,"\""+text+"\" Copied to clipboard",Toast.LENGTH_SHORT).show();
			ClipboardManager clipboard = (ClipboardManager) con.getSystemService(Context.CLIPBOARD_SERVICE); 
 			ClipData clip = ClipData.newPlainText("key", text);

 			clipboard.setPrimaryClip(clip);
		}

		public CListener(Context c,String t){
			con=c;
			text=t;
		}
	}
}
