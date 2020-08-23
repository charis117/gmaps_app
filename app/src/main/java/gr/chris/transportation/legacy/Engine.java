package gr.chris.transportation.legacy;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Engine
{

	public static String thisFolder="/storage/emulated/0/AppProjects/Transportation/src/";
	public ListActivity con;
	public static void main(String args[]){
		try{
			Engine rm=new Engine(null);
		}catch(IOException e){
			e.printStackTrace();
		}

	}
	ArrayList<Integer>[] routes=new ArrayList[537];
	Route[] routesInfo=new Route[537];
	Station[] stations=new Station[8108];
	Municipality[] municipalities=new Municipality[53];
	public Engine(ListActivity c) throws IOException{
		con=c;
		debug("Allocating Memory...");
		
		debug("Memory Allocated");
		debug("Reading stops.csv...");
		BufferedReader s=new BufferedReader(is("stops.csv"));
		BufferedReader bf=new BufferedReader(is("stops.csv"));
		
		s.readLine();
		int iter=0;
		while(iter<21925){
			String line=s.readLine();
			//println(line);
			String[] nums=line.split(",");
			int[] Inums=new int[nums.length];
			for(int i=0;i!=nums.length;i++){
				Inums[i]=number(nums[i]);
			}
			//println(iter+"-"+Inums[0]);
			if(routes[Inums[0]]==null){
				routes[Inums[0]]=new ArrayList<Integer>();
			}
			routes[Inums[0]].add(Inums[1]);
			iter++;
		}
		debug("Done!\nReading routes.csv...");
		s.close();
		BufferedReader s2=new BufferedReader(is("routes3.txt"));{
			s2.readLine();
			for(int i=1;i!=537;i++){
				String[] vals=s2.readLine().split(",");
				routesInfo[i]=new Route(vals[2],vals[1],vals[10],vals[9],i);
			}
		}
		s2.close();
		debug("Done!\nRading markers.csv...");
		BufferedReader s3=new BufferedReader(is("markers3.txt"));{
			s3.readLine();
			for(int i=1;i!=8108;i++){
				String[] vals=s3.readLine().split(",");
				//stations[i]=new Station(vals[0],vals[1],Float.parseFloat(vals[2]),Float.parseFloat(vals[3]),number(vals[4]),number(vals[5]));
				stations[i]=new Station(vals[0],vals[1],Float.parseFloat(vals[2]),Float.parseFloat(vals[3]),number(vals[4]),number(vals[5]));
			}
		}
		debug("Done!\nRading municipalities.csv...");
		BufferedReader s4=new BufferedReader(is("municipalities.csv"));{
			s4.readLine();
			for(int i=1;i!=52;i++){
				String[] vals=s4.readLine().split(",");
				//stations[i]=new Station(vals[0],vals[1],Float.parseFloat(vals[2]),Float.parseFloat(vals[3]),number(vals[4]),number(vals[5]));
				municipalities[i]=new Municipality(vals[0],vals[1],vals[6],number(vals[9]));
			
			}
		}
		debug("Done!");
	
		//BufferedReader uS=new BufferedReader(System.in);
		//println("Agencies: 1:OASA 2:STASY 3:OSE");
		//String uAg=uS.readLine();
		ArrayList<Station> visi=new ArrayList<Station>();
		for(Station st:stations){
			if(st==null){
				continue;
			}
			
				//print(st.name);
				if(st.municipalityId>0){
					if(municipalities[st.municipalityId]!=null){
						//print("-");
						//print(municipalities[st.municipalityId].city);
					}
				}
				//print("-");
				//println(st.stationId);
				
			
		}
		con.flu();
			for(Station st:stations){
				if(st==null){
					continue;
				}
				
					String stat="";
					stat=st.name;
					if(st.municipalityId>0){
						if(municipalities[st.municipalityId]!=null){
							
							stat+="-"+municipalities[st.municipalityId].city;
						}}
					stat+="-"+st.stationId;
					println(stat);
					visi.add(st);
					//print("-");
					//println(st.stationId);
					
				
			
			
		}
		con.Statshow=visi;
		con.flu();
		
	}


	public ArrayList<Route> func(String stationId){
		String out="";
		ArrayList<Route> arr=new ArrayList<Route>();
		
		for(int i=0;i<routes.length;i++){
			ArrayList<Integer> route=routes[i];
			if(route==null){
				continue;
			}
			for(int stati:route){
				
				if(stati==number(stationId)){
					out=routesInfo[i].busId+"-"+routesInfo[i].routeName+"\n";
					arr.add(routesInfo[i]);
					
				}
			}
			//con.flu();
		}
		//con.flu();
		return arr;
	}
	public static class Route{
		String routeName;
		int index;
		String busId;
		String routeId;
		String Agency;
		String AgencyName;
		public Route(String routeName,String busId,String routeId,String Agency,int i){
			this.routeName=routeName;
			this.busId=busId;
			this.routeId=routeId;
			this.Agency=Agency;
			this.index=i;
			switch(Agency){
				case "1":
					AgencyName="OASA";
					break;
				case "2":
					AgencyName="STASY";
					break;
				case "3":
					AgencyName="OSE";
					break;

			}
		}
	}
	public static class Station{
		String code;
		String name;
		float latitude;
		float longtitude;
		int municipalityId;
		int stationId;
		public Station(String code,String name,float latitude,float longtitude,int municipalityId,int stationId){
			this.code=code;
			this.name=name;
			this.latitude=latitude;
			this.longtitude=longtitude;
			this.municipalityId=municipalityId;
			this.stationId=stationId;
		}
		public String fullDescription(){
			return code+"-"+name+" Lat:"+(int)(latitude*1000.0)+" Lon:"+(int)(longtitude*1000.0)+" ID:"+stationId;
		}
	}

	public void debug(String s){
		//println("DEBUG:"+s);
	}
	public int number(String s){
		int out=0;
		for(int i=0;i!=s.length();i++){
			int ck=(int)s.charAt(i);
			if(ck>=48&&ck<=57){
				out*=10;
				out+=(ck-48);
			}
		}
		return out;
	}
	
	class Municipality{
		String name;//0
		String mayor;//1
		String city;//6
		int id;//9
		public Municipality(String n,String m,String c,int i){
			name=chop(n);
			mayor=chop(m);
			city=chop(c);
			id=i;
		}
	}

	public static String chop(String s){
		return  s.substring(1,s.length()-1);
	}
	
	public InputStreamReader is(String s) throws IOException{
		AssetManager assets=con.getAssets();
		InputStream in=assets.open(s);
		return new InputStreamReader(in);
	}
	
	public void println(Object s){
		/*runOnUiThread(new Runnable(){
				public void run(){
					newText(s);
				}
			});*/
		//con.add(s);
		con.predef.add(s.toString());
	}
	public void print(Object s){
		/*runOnUiThread(new Runnable(){
		 public void run(){
		 newText(s);
		 }
		 });*/
		con.addc(s);
	}
	
	/*int number(String s){
		int out=0;
		for(int i=0;i<s.length();i++){
			char c=s.charAt(i);
			if(c>=48&&c<=57){
				out*=10;
				out+=(int)c;
			}
		}
		return out;
	}*/

}
