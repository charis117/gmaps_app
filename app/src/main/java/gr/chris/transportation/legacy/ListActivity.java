package gr.chris.transportation.legacy;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import gr.chris.transportation.R;

public class ListActivity extends Activity 
{
	ArrayList<String> predef=new ArrayList<String>();
	ArrayList<Engine.Station> Statshow;
	ArrayList<String> show=new ArrayList<String>();
	ArrayList<Integer> inds=new ArrayList<Integer>();
	
	ListView sv;
	EditText et;
	Engine statsearch;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listmain);
		sv=(ListView)findViewById(R.id.listsv);
		
		et=(EditText)findViewById(R.id.mainEditText);
		
		//predef.add((int)c+": "+c);
		for(int i=0;i<predef.size();i++){
			inds.add(i);
		}
		setList(predef);
		
		sv.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					String s=(String)p1.getAdapter().getItem(p3);
					String stationId=s.substring(s.lastIndexOf("-")+1,s.length());
					//Toast.makeText(getApplicationContext(),stationId,Toast.LENGTH_SHORT).show();
					ArrayList<Engine.Route> rarr=statsearch.func(stationId);
					
					//new CAlertDialog(LOCATION_SERVICE.this,String.valueOf(ch),"Charachter:"+cha+"  \n"+"Bin:"+Integer.toBinaryString(ch)+" \nHex:"+Integer.toHexString(ch).toUpperCase(),"OK",false);
					//Toast.makeText(ListActivity.this,stationId+"-"+rarr.size(),Toast.LENGTH_LONG).show();
					Engine.Station st=Statshow.get(inds.get(p3));
					/*Engine.Station[] nearS=nearStats(st);
					StringBuilder ssb=new StringBuilder();
					ssb.append("Κοντινοί σταθμοί:\n");
					for(Engine.Station is: nearS){
						ssb.append(is.name);
						ssb.append("\n");
					}*/
					//new CAlertDialog(ListActivity.this,st.name,ssb.toString(),"OK",false,CAlertDialog.ACTION_HIDE_DIALOG);
					new CAlertDialog(ListActivity.this,s,"",st,rarr,"OK",false,null);

				}


			});
		sv.setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4){
					Engine.Station s=Statshow.get(inds.get(p3));
					String inf=s.fullDescription();
					new CAlertDialog(ListActivity.this,s.name,inf,"OK",false,CAlertDialog.ACTION_HIDE_DIALOG);
					return true;
				}

			
		});
		et.addTextChangedListener(new TextWatcher(){

				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
					//Toast.makeText(MainActivity.this,p1.toString(),Toast.LENGTH_SHORT).show();
					find(p1.toString());
				}


			});
			predef.clear();
			
		try{
			statsearch=new Engine(this);
		}catch (IOException e){
			e.printStackTrace();
		}
		for(int i=0;i<Statshow.size();i++){
			inds.add(i);
		}
    }

    
	public void search(View v){
		hideKeyboard(this);
	}
	public void setList(ArrayList<String> ar){
		sv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ar));
		Toast.makeText(this,"",12);
	}
	public void find(String s){
		s=s.toUpperCase();
		show.clear();
		inds.clear();
		for(int i=0;i<predef.size();i++){
			String ite=predef.get(i).toUpperCase();
			if(ite.contains(s)){
				show.add(ite);
				inds.add(i);
			}
		}
		
		setList(show);
	}
	
	
	String buff="";
	
	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = activity.getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = new View(activity);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void add(Object s){
		buff+=s.toString()+"\n";
		//tv.setText(buff);
	}
	public void addc(Object s){
		buff+=s.toString();
		//tv.setText(buff);
	}

	public void flu(){
		//tv.setText(buff);
		//predef.clear();
		//for(String ite:buff.split(("\n"))){
		//		predef.add(ite);
		//}
		setList(predef);
	}
	
	
	public Engine.Station[] nearStats(final Engine.Station s){
		Toast.makeText(this,s.name,0).show();
		ArrayList<Engine.Station> near=(ArrayList<Engine.Station>)Statshow.clone();
		Collections.sort(near,new Comparator<Engine.Station>(){

				@Override
				public int compare(Engine.Station p1, Engine.Station p2){
					double dist1=Math.sqrt(Math.pow(s.latitude-p1.latitude,2)+Math.pow(s.longtitude-p1.longtitude,2));
					double dist2=Math.sqrt(Math.pow(s.latitude-p2.latitude,2)+Math.pow(s.longtitude-p2.longtitude,2));
					
					return (int)(dist2-dist1);
				}


			});
			return new Engine.Station[]{near.get(0),near.get(1),near.get(2),near.get(3)};
	}
}
