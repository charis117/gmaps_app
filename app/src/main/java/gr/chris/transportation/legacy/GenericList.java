package gr.chris.transportation.legacy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import gr.chris.transportation.R;

public class GenericList extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glist);
		ListView lv=(ListView)findViewById(R.id.glistListView);
		List<String> vals=getIntent().getStringArrayListExtra("data");
		getActionBar().setTitle(getIntent().getStringExtra("title"));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,vals);
		//Log.v("Adapter",dataAdapter.toString());
		//Log.v("List View",lv.toString());
		lv.setAdapter(dataAdapter);
		lv.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{

					Intent data = getIntent();
					int requestCode=data.getIntExtra("request",0);
					if(requestCode!=0){
						data.setData(Uri.parse(String.valueOf(p3)));
						setResult(RESULT_OK, data); 
						//---close the activity--- 
						finish();
					}
				}


			});

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
				// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class CustomList extends ArrayAdapter<String>{


		private final Activity context;

		private final List<String> list,list2;
		public int selected=0;



		public CustomList(Activity context,

						  List<String> list,List<String> list2, int selected) {

			super(context, R.layout.list_single, list);

			this.context = context;

			this.list=list;
			this.list2=list2;
			this.selected=selected;




		}

		@Override

		public View getView(int position, View view, ViewGroup parent) {

			LayoutInflater inflater = context.getLayoutInflater();

			int viewID=0;
			if(list2.size()!=0){
				viewID=R.layout.list_single_sub;

			}else{
				viewID=R.layout.list_single;
			}
			View rowView= inflater.inflate(viewID, null, true);
			if(selected==50){
				rowView=inflater.inflate(R.layout.list_single_t,null,true);

			}

			TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

			TextView txtsubTitle = (TextView) rowView.findViewById(R.id.subtxt);



			txtTitle.setText(list.get(position));
			if(list2.size()!=0){
				txtsubTitle.setVisibility(View.VISIBLE);
				txtsubTitle.setText(" "+list2.get(position));
			}
			if(selected==position){
				txtTitle.setTextColor(Color.RED);
			}


			return rowView;

		}

	}
}
