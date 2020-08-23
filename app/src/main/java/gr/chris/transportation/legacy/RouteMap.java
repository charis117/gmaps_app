package gr.chris.transportation.legacy;

import java.util.*;
import java.io.*;

public class RouteMap{
	
	public static String thisFolder="/storage/emulated/0/AppProjects/Transportation/src/";
	
	public static void main(String args[]){
		try{
			RouteMap rm=new RouteMap();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public RouteMap() throws IOException{
		debug("Allocating Memory...");
		ArrayList<Integer>[] routes=new ArrayList[537];
		Route[] routesInfo=new Route[537];
		Station[] stations=new Station[8108];
		Municipality[] municipalities=new Municipality[53];
		debug("Memory Allocated");
		debug("Reading stops.csv...");
		Scanner s=new Scanner(new File(thisFolder+"stops.csv"));
		s.nextLine();
		while(s.hasNext()){
			String line=s.nextLine();
			String[] nums=line.split(",");
			int[] Inums=new int[nums.length];
			for(int i=0;i!=nums.length;i++){
				Inums[i]=Integer.parseInt(nums[i]);
			}
			if(routes[Inums[0]]==null){
				routes[Inums[0]]=new ArrayList<Integer>();
			}
			routes[Inums[0]].add(Inums[1]);
		}
		debug("Done!\nReading routes.csv...");
		s.close();
		Scanner s2=new Scanner(new File(thisFolder+"routes3.txt"));{
			s2.nextLine();
			for(int i=1;i!=537;i++){
				String[] vals=s2.nextLine().split(",");
				routesInfo[i]=new Route(vals[2],vals[1],vals[10],vals[9]);
			}
		}
		s2.close();
		debug("Done!\nRading municipalities.csv...");
		Scanner s4=new Scanner(new File(thisFolder+"municipalities.csv"));{
			s4.nextLine();
			for(int i=1;i!=52;i++){
				String[] vals=s4.nextLine().split(",");
				//stations[i]=new Station(vals[0],vals[1],Float.parseFloat(vals[2]),Float.parseFloat(vals[3]),Integer.parseInt(vals[4]),Integer.parseInt(vals[5]));
				municipalities[i]=new Municipality(vals[0],vals[1],vals[6],number(vals[9]));

			}
		}
		debug("Done!\nRading markers.csv...");
		Scanner s3=new Scanner(new File(thisFolder+"markers3.txt"));{
			s3.nextLine();
			for(int i=1;i!=8108;i++){
				String[] vals=s3.nextLine().split(",");
				//stations[i]=new Station(vals[0],vals[1],Float.parseFloat(vals[2]),Float.parseFloat(vals[3]),Integer.parseInt(vals[4]),Integer.parseInt(vals[5]));
				Municipality mun=null;
				if(number(vals[4])>0){
					mun=municipalities[number(vals[4])];
				}
				stations[i]=new Station(vals[0],vals[1],Float.parseFloat(vals[2]),Float.parseFloat(vals[3]),number(vals[4]),mun,number(vals[5]));
			
			}
		}
		
		debug("Done!");
		Scanner uS=new Scanner(System.in);
		System.out.println("Agencies: 1:OASA 2:STASY 3:OSE");
		String uAg=uS.nextLine();
		System.out.println("Available buses:");
		for(Route r:routesInfo){
			if(r==null){
				continue;
			}
			if(r.Agency.equals(uAg)){
				System.out.print(r.busId);
				System.out.print("-");
				System.out.print(r.routeName);
				System.out.print("-");
				System.out.println(r.routeId);
			}
		}
		String busId=uS.nextLine();
		System.out.println();
		int routeId=0;
		for(Route r:routesInfo){
			if(r==null){
				continue;
			}
			if(r.busId.equals(busId)){
				routeId=number(r.routeId);
				break;
			}
		}
		int minLat=0;
		int maxLat=0;
		int minLon=0;
		int maxLon=0;
		double mul=10000.0;
		int mod=2500;

		for(Integer i :routes[routeId]){
			Station st=stations[i];
			System.out.println(st.fullDescription());
			
			if((int)(st.latitude*mul)<minLat){
				minLat=(int)(st.latitude*mul);
			}
			if((int)(st.latitude*mul)>maxLat){
				maxLat=(int)(st.latitude*mul);
			}
			if((int)(st.longtitude*mul)<minLon){
				minLon=(int)(st.longtitude*mul);
			}
			if((int)(st.longtitude*mul)>maxLon){
				maxLon=(int)(st.longtitude*mul);
			}
		}
		//BufferedImage img=new BufferedImage(maxLat-minLat,maxLon-minLon,BufferedImage.TYPE_INT_RGB);
		
		
	}
	class Route{
		String routeName;
		String busId;
		String routeId;
		String Agency;
		String AgencyName;
		public Route(String routeName,String busId,String routeId,String Agency){
			this.routeName=routeName;
			this.busId=busId;
			this.routeId=routeId;
			this.Agency=Agency;
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
	class Station{
		String code;
		String name;
		float latitude;
		float longtitude;
		int municipalityId;
		int stationId;
		Municipality m;
		public Station(String code,String name,float latitude,float longtitude,int municipalityId,Municipality m,int stationId){
			this.code=code;
			this.name=name;
			this.latitude=latitude;
			this.longtitude=longtitude;
			this.municipalityId=municipalityId;
			this.stationId=stationId;
			this.m=m;
		}
		public String fullDescription(){
			return code+"-"+name+" Lat:"+(int)(latitude*1000.0)+" Lon:"+(int)(longtitude*1000.0)+(m==null ? " " : " "+m.city+",")+"ID:"+stationId;
		}
	}
	
	public void debug(String s){
		System.out.println("DEBUG:"+s);
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
		
	
}
