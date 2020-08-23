package gr.chris.transportation.legacy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import gr.chris.transportation.R;
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
	CAlertDialog(final Activity context,String title,String message,final String button,boolean cancel,final int action){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		//final MainActivity Ma=(MainActivity)context;
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder .setMessage(message) .setCancelable(cancel) .setPositiveButton(button,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					switch(action){
						case 100:
							context.finish();

							break;
						case 200:
							dialog.cancel();

							break;

					}
				}
			});
		if(cancel){
			alertDialogBuilder.setNegativeButton(cancel_button,new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog,int id){
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




	public CAlertDialog(final Activity context, String title, String message, final Engine.Station s, final ArrayList<Engine.Route> ar, final String button, boolean cancel, final SQLEngine ss){
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
					String info="Πάροχος:"+r.AgencyName;
					info+="\nΔρομολόγιο:\n"+r.routeName;
					info+="\nID:"+r.routeId;
					new CAlertDialog(context,r.busId,info,"OK",false,ACTION_HIDE_DIALOG);

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


	public static class ChoseListener{
		public AlertDialog dialog=alertDialog;
		public void onChose(int choice){

		}
		public void onChoseAffirmative(){

		}
		public void onChoseNegative(){

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
