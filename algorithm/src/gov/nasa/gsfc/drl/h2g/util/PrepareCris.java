/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;


import java.io.File;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;




public class PrepareCris {
     private static final int RADIANCE_DATA=3;
     private static final int GEOLOCATION_DATA=4;
     private static final int QF4LW_RDR_FLAG_OFFSET=0;
     private static final int QF4MW_RDR_FLAG_OFFSET=1;
     private static final int QF4SW_RDR_FLAG_OFFSET=2;

	/****************************************************************************
	 * main.
	 ****************************************************************************/
	public static void main(String[] args) throws Exception {
		
		
		
		long[] dimslw,dimsmw,dimssw,dimsgeo,dimsqf3;
		
		int status_n;
		Boolean hrd_flag=true;
		File inputCrisFile,outputCrisFile,inputCrisGeoFile;
		inputCrisFile = new File(args[0]);
		inputCrisGeoFile = new File(args[1]);
		outputCrisFile = new File(args[2]);
		if(args.length>3)
		  if(args[3].equals("-nohrd"))
			  hrd_flag=false;
		float TwoDdata[][] = null;
		int QF4_LW_RDR_Flag[][] = null;
		int QF4_MW_RDR_Flag[][] = null;
		int QF4_SW_RDR_Flag[][] = null;
		int[] output_ids=null;
		int[] input_ids=new int[11];
		float[] lw_v=new float[717];
		float[] mw_v=new float[437];
		float[] sw_v=new float[163];
		
		double startv=650.0-0.625*2;
		double stopv=1095.0+0.625*2;
		
		
		
		lw_v= PrepareCris.populate_channel_wavenumber(648.75,0.625,717);
		mw_v= PrepareCris.populate_channel_wavenumber(1207.5,1.25,437);
		sw_v= PrepareCris.populate_channel_wavenumber(2150,2.5,163);
		
				
		try {
			input_ids[0] = H5.H5Fopen(inputCrisFile.toString(), HDF5Constants.H5F_ACC_RDONLY,HDF5Constants.H5P_DEFAULT);
			input_ids[1] = H5.H5Fopen(inputCrisGeoFile.toString(), HDF5Constants.H5F_ACC_RDONLY,HDF5Constants.H5P_DEFAULT);
			
			if (input_ids[0] >= 0 && input_ids[1]>=0){
				
				input_ids[2] = H5.H5Dopen(input_ids[0], "/All_Data/CrIS-SDR_All/ES_RealLW",HDF5Constants.H5P_DATASET_ACCESS_DEFAULT );
				input_ids[3] = H5.H5Dopen(input_ids[0], "/All_Data/CrIS-SDR_All/ES_ImaginaryLW",HDF5Constants.H5P_DATASET_ACCESS_DEFAULT );
				dimslw  = PrepareCris.readDimensions(input_ids[2]);
				
				input_ids[4] = H5.H5Dopen(input_ids[0], "/All_Data/CrIS-SDR_All/ES_RealMW",HDF5Constants.H5P_DATASET_ACCESS_DEFAULT );
				input_ids[5] = H5.H5Dopen(input_ids[0], "/All_Data/CrIS-SDR_All/ES_ImaginaryMW",HDF5Constants.H5P_DATASET_ACCESS_DEFAULT );
				dimsmw  = PrepareCris.readDimensions(input_ids[4]);
				
				input_ids[6] = H5.H5Dopen(input_ids[0], "/All_Data/CrIS-SDR_All/ES_RealSW",HDF5Constants.H5P_DATASET_ACCESS_DEFAULT );
				input_ids[7] = H5.H5Dopen(input_ids[0], "/All_Data/CrIS-SDR_All/ES_ImaginarySW",HDF5Constants.H5P_DATASET_ACCESS_DEFAULT);
				dimssw  = PrepareCris.readDimensions(input_ids[6]);
				
				input_ids[8] = H5.H5Dopen(input_ids[1], "/All_Data/CrIS-SDR-GEO_All/Latitude",HDF5Constants.H5P_DATASET_ACCESS_DEFAULT);
				input_ids[9] = H5.H5Dopen(input_ids[1], "/All_Data/CrIS-SDR-GEO_All/Longitude",HDF5Constants.H5P_DATASET_ACCESS_DEFAULT);
			    dimsgeo = PrepareCris.readDimensions(input_ids[8]);
			    
			    input_ids[10] = H5.H5Dopen(input_ids[0], "/All_Data/CrIS-SDR_All/QF4_CRISSDR",HDF5Constants.H5P_DATASET_ACCESS_DEFAULT);
			    dimsqf3 = PrepareCris.readDimensions(input_ids[10]);
			    
			    if(dimsgeo[0] != dimslw[0]){
			    	System.out.println("Dimension Mismatch between SCRIS and GCRSO");
			    	System.exit(1);
			    }
			    
			    
			    //Now let's create the output file with the datasets
			    output_ids=PrepareCris.createOutputFile(outputCrisFile.toString(),(int)dimsgeo[0]*3,(int)dimsgeo[1]*3);
			    
			    
			    for(int row=0;row<dimsgeo[0];row++){//dimsgeo[0]
			    	if(row%10==0)
			    	System.out.println("Processing ROW "+row);
			    	
			    	TwoDdata=PrepareCris.readRow(input_ids[2], dimslw,row, lw_v,402,411,RADIANCE_DATA,hrd_flag);
			    	PrepareCris.writeRow(output_ids[1], TwoDdata,3,90,row*3);
			    	
			    	/*TwoDdata=PrepareCris.readRow(input_ids[2], dimslw,row,null);
			    	PrepareCris.writeRow(output_ids[2], TwoDdata,3,90,row*3);*/
			    	
                    TwoDdata=PrepareCris.readRow(input_ids[4], dimsmw,row,mw_v,313,316,RADIANCE_DATA,hrd_flag);
                    PrepareCris.writeRow(output_ids[2], TwoDdata,3,90,row*3);
			    	
			    	/*TwoDdata=PrepareCris.readRow(input_ids[4], dimsmw,row,null);
			    	PrepareCris.writeRow(output_ids[4], TwoDdata,3,90,row*3);*/
			    	
                    TwoDdata=PrepareCris.readRow(input_ids[6], dimssw,row,sw_v,110,113,RADIANCE_DATA,hrd_flag);
                    PrepareCris.writeRow(output_ids[3], TwoDdata,3,90,row*3);
			    	
			    	/*TwoDdata=PrepareCris.readRow(input_ids[6], dimssw,row,null);
			    	PrepareCris.writeRow(output_ids[6], TwoDdata,3,90,row*3);*/
                    
                    QF4_LW_RDR_Flag=PrepareCris.readQF4(input_ids[10], dimsqf3,row,QF4LW_RDR_FLAG_OFFSET);
                    QF4_MW_RDR_Flag=PrepareCris.readQF4(input_ids[10], dimsqf3,row,QF4MW_RDR_FLAG_OFFSET);
                    QF4_SW_RDR_Flag=PrepareCris.readQF4(input_ids[10], dimsqf3,row,QF4SW_RDR_FLAG_OFFSET);
                    
                    //PrepareCris.writeintRow(output_ids[10], QF4_SW_RDR_Flag,3,90,row*3);
                    
			    	
                    TwoDdata=PrepareCris.readRow(input_ids[8], dimsgeo,row,null,-999,-999, GEOLOCATION_DATA,hrd_flag);
                    
                    //TwoDdata=PrepareCris.adjustGeolocation(TwoDdata,QF4_LW_RDR_Flag);
                    PrepareCris.writeRow(output_ids[4], TwoDdata,3,90,row*3);
                    //TwoDdata=PrepareCris.adjustGeolocation(TwoDdata,QF4_MW_RDR_Flag);
                    PrepareCris.writeRow(output_ids[6], TwoDdata,3,90,row*3);
                    //TwoDdata=PrepareCris.adjustGeolocation(TwoDdata,QF4_SW_RDR_Flag);
                    PrepareCris.writeRow(output_ids[8], TwoDdata,3,90,row*3);
			    	
			    	TwoDdata=PrepareCris.readRow(input_ids[9], dimsgeo,row,null,-999,-999, GEOLOCATION_DATA,hrd_flag);
			    	//TwoDdata=PrepareCris.adjustGeolocation(TwoDdata,QF4_LW_RDR_Flag);
			    	PrepareCris.writeRow(output_ids[5], TwoDdata,3,90,row*3);
			    	//TwoDdata=PrepareCris.adjustGeolocation(TwoDdata,QF4_MW_RDR_Flag);
			    	PrepareCris.writeRow(output_ids[7], TwoDdata,3,90,row*3);
			    	//TwoDdata=PrepareCris.adjustGeolocation(TwoDdata,QF4_SW_RDR_Flag);
			    	PrepareCris.writeRow(output_ids[9], TwoDdata,3,90,row*3);
			    }
			    	
			    	
			 }//end of if files exist
			 //Close the output File
			 PrepareCris.closeOutputFile(output_ids);
			 PrepareCris.closeInputFiles(input_ids);
			 
			}//end of try
			    
			/*file_id = H5.H5Fcreate(FILENAME, HDF5Constants.H5F_ACC_TRUNC,
						HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);   
			  TwoDdata=PrepareCris.readRow(H5dataset_reallw, 0);
			  PrepareCris.writeRow(outputCrisFile.toString(),TwoDdata);
			}
			    
		}*/
		catch (Exception e) {
		
		}
		
	}
	
   

	private static long[] readDimensions(int H5dataset_id){
      
      long[] dims=null;
      try{
    	//get rank and dimensions of lw sdr
		    //int H5Datatype=H5.H5Dget_type(H5dataset_id);
			int H5dataspace = H5.H5Dget_space(H5dataset_id);   
			int H5rank = H5.H5Sget_simple_extent_ndims(H5dataspace);
			//System.out.println("Rank="+H5rank);
			dims=new long[H5rank];
			long[] H5max_dims=new long[H5rank];
			int status  = H5.H5Sget_simple_extent_dims(H5dataspace, dims, H5max_dims);
			H5.H5Sclose(H5dataspace);
      }
      catch (Exception e) {
  		
	  }
      return(dims);
    }
    
	private static int [][] readQF4(int H5dataset_id, long[] dims, int rowno, int QF4_FLAG_OFFSET){
		
		int QF4_Flag[][] = null;
		
        int H5rank=(int)dims.length;
		
		try {
		  
		   int H5dataspace = H5.H5Dget_space(H5dataset_id);
		   /*The data set is either 3D or 4D This is CrIS specific*/
	       long offset[]=new long[H5rank];
	       long count[]=new long[H5rank];
	       long stride[]=new long[H5rank];
	       long block[]=new long[H5rank];
	       
	       //parameters for memory data space
	       long dimsm[]=new long[H5rank];
	       long dimsm_max[]=new long[H5rank];
	       
	       for(int i=0;i<H5rank;i++)
	       {
	        	//System.out.println("dims["+i+"]="+dims[i]);
	        	stride[i]=1; block[i]=1;
	        	if(i==0) {
	        		offset[i]=rowno;
	        		count[i]=1;
	        		dimsm[i]=1;
	        		dimsm_max[i]=1;
	        	}
	        	else if (i==3){
	        	   offset[i]=QF4_FLAG_OFFSET;	
	        	   count[i]=1;
	 	    	   dimsm[i]=1;
	 	    	   dimsm_max[3]=1;
	        	}
	        	else {
	        		offset[i]=0;
	        		count[i]=dims[i];	
	        		dimsm[i]=dims[i];
	        		dimsm_max[i]=dims[i];
	        	}
	        	//System.out.println(offset[i]);
	       }
	       
	       /*Define data set hyperslab*/
    	   int status = H5.H5Sselect_hyperslab(H5dataspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);

    	   /*Define the memory dataspace.*/      
           int memspace = H5.H5Screate_simple(H5rank,dimsm,dimsm_max);   
	       /* Define memory hyperslab.*/
           offset[0]=0;
           offset[3]=0;
	       status = H5.H5Sselect_hyperslab(memspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);
	       
	       QF4_Flag=new int[3][(int)dims[1]*3];
	       	       
	       int twoDrow,twoDcol;
		   if (H5dataset_id >= 0)
		   {		  
			   
			      byte FourDQFdata[][][][]=new byte[1][(int)dims[1]][(int)dims[2]][1];			      
			      H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_UINT8, memspace, H5dataspace,	HDF5Constants.H5P_DEFAULT, FourDQFdata);
		          for(int i=0;i<1;i++)
		        	  for(int j=0;j<dims[1];j++)
		        		  for(int k=0;k<dims[2];k++){
		        			  twoDrow=(i*3)+ (k/3);
		        			  twoDcol=(j*3)+ (k%3);			        			  
		        			  QF4_Flag[twoDrow][twoDcol]=(FourDQFdata[i][j][k][0] & 0x02) >> 1;		        			  
		        			  //System.out.println(i+","+j+","+k+">> "+FourDQFdata[i][j][k][0]+","+QF4_Flag[twoDrow][twoDcol]);
		        		  }		
			    
				      
			   			   
			  
			   
		   }
				
		   H5.H5Sclose(H5dataspace);
		}catch (Exception e) {
			System.out.println(e.toString());
		}

		
		return QF4_Flag;
	}

	private static float [][] readRow(int H5dataset_id, long[] dims, int rowno, float[] v,int channel_start, int channel_end, int DATA_TYPE,Boolean hrd_flag){
		
		float TwoDdata[][] = null;		
		int H5rank=(int)dims.length;
		
		//System.out.println("in readRow");
		//if(v==null)
		//	System.out.println("v is null");
		//System.out.println("freq 1:"+v[0]);
		try {
		  
		   int H5dataspace = H5.H5Dget_space(H5dataset_id);
		   /*The data set is either 3D or 4D This is CrIS specific*/
	       long offset[]=new long[H5rank];
	       long count[]=new long[H5rank];
	       long stride[]=new long[H5rank];
	       long block[]=new long[H5rank];
	       
	       //parameters for memory data space
	       long dimsm[]=new long[H5rank];
	       long dimsm_max[]=new long[H5rank];
	       
	       for(int i=0;i<H5rank;i++)
	       {
	        	//System.out.println("dims["+i+"]="+dims[i]);
	        	stride[i]=1; block[i]=1;
	        	if(i==0) {
	        		offset[i]=rowno;
	        		count[i]=1;
	        		dimsm[i]=1;
	        		dimsm_max[i]=1;
	        	}
	        	else {
	        		offset[i]=0;
	        		count[i]=dims[i];	
	        		dimsm[i]=dims[i];
	        		dimsm_max[i]=dims[i];
	        	}
	        	//System.out.println(offset[i]);
	       }
	      
	       /*Define data set hyperslab*/
    	   int status = H5.H5Sselect_hyperslab(H5dataspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);

    	   /*Define the memory dataspace.*/      
           int memspace = H5.H5Screate_simple(H5rank,dimsm,dimsm_max);   
	       /* Define memory hyperslab.*/
           offset[0]=0;
	       status = H5.H5Sselect_hyperslab(memspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);
	       
	       TwoDdata=new float[3][(int)dims[1]*3];
	       
	       int twoDrow,twoDcol;
		   if (H5dataset_id >= 0)
		   {
			   switch(DATA_TYPE){
			   case GEOLOCATION_DATA:float ThreeDdata[][][]=new float[1][(int)dims[1]][(int)dims[2]];			         
				      H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_FLOAT, memspace, H5dataspace,	HDF5Constants.H5P_DEFAULT, ThreeDdata);
				      //System.out.println(ThreeDdata[0][0][0]);
				      //System.out.println("dims="+dims[0]+","+dims[1]+","+dims[2]);
			          for(int i=0;i<1;i++)
			        	  for(int j=0;j<dims[1];j++)
			        		  for(int k=0;k<dims[2];k++){
			        			  twoDrow=(i*3)+ (k/3);
			        			  twoDcol=(j*3)+ (k%3);			        			  
			        			  //System.out.println(i+","+j+","+k+"->"+twoDrow+","+twoDcol);
			        			  if(hrd_flag)
			        			  {
			        			    if(k==3)
			        				  TwoDdata[twoDrow][twoDcol]=(float) -999.0;
			        			    else
			        			      TwoDdata[twoDrow][twoDcol]=ThreeDdata[i][j][k];
			        			  }else
			        				  TwoDdata[twoDrow][twoDcol]=ThreeDdata[i][j][k];
			        			  //System.out.println(TwoDdata[twoDrow][twoDcol]+","+ThreeDdata[i][j][k]);
			        		  }
			        			  
			        			  
			 
				      break;				      
			
				      
			   case RADIANCE_DATA:				     
				     float FourDdata[][][][]=new float[1][(int)dims[1]][(int)dims[2]][(int)dims[3]];
				     float bt;
				     Boolean fillvalue=false;
				     H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_FLOAT, memspace, H5dataspace,	HDF5Constants.H5P_DEFAULT, FourDdata);
			         //System.out.println(FourDdata[0][0][0][0]);
			         //System.out.println("dims="+dims[0]+","+dims[1]+","+dims[2]+","+dims[3]);
			          for(int i=0;i<1;i++)
		        	    for(int j=0;j<dims[1];j++)
		        		  for(int k=0;k<dims[2];k++){
		        			  twoDrow=(i*3)+ (k/3);
		        			  twoDcol=(j*3)+ (k%3);
		        			  //System.out.println(i+","+j+","+k+"->"+twoDrow+","+twoDcol);
		        			  float sum=0;
		        			  fillvalue=false;
		        			  for(int l=channel_start;l<channel_end;l++){
		        				  if(FourDdata[i][j][k][l]<=-999)
		        					  fillvalue=true;
		        				  if(v==null)
		        					  sum+=FourDdata[i][j][k][l];
		        				  else
		        				  {
		        					  bt=convertRad2BT(FourDdata[i][j][k][l],v[l]);
		        					  //if(i==0 && j==0 && k==0)
		        					  //  System.out.println(l+":"+v[l]+";"+FourDdata[i][j][k][l]+": bt="+bt);
		        				      sum+=bt;
		        				  }
		        			  }
		        			  if(fillvalue==false)
		        			  {
		        			     float avg=sum/(float)(channel_end-channel_start);		        			  
		        			     TwoDdata[twoDrow][twoDcol]=avg;
		        			  }
		        			  else TwoDdata[twoDrow][twoDcol]=(float)-999.8;
		        		  }
                      break;				   
			   }
			   
		   }
				
		   H5.H5Sclose(H5dataspace);
		}catch (Exception e) {
			System.out.println(e.toString());
		}
		//System.out.println("finished readRow");
		return TwoDdata;
		
	}
	

	private static float [][] adjustGeolocation(float[][] inGeoData, int[][] QF4_RDR_Flag){
				
	    int rows=inGeoData.length;
	    int cols=inGeoData[0].length;
		
		
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				if (QF4_RDR_Flag[i][j] !=0) inGeoData[i][j]=(float)-999.0;
		
		return inGeoData;
	}
	
	private static int[] createOutputFile(String FILENAME,int nRows, int nCols){
		int[] ids=new int[10];
		int dataspace_id=-1;
		long[] dims=new long[2];
		dims[0]=(long)nRows;dims[1]=(long)nCols;
		
		// Create a new file using default properties.
		try {
			ids[0] = H5.H5Fcreate(FILENAME, HDF5Constants.H5F_ACC_TRUNC,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			dataspace_id = H5.H5Screate_simple(dims.length, dims, null);
			if ((ids[0] >= 0) && (dataspace_id >= 0)){
				ids[1] = H5.H5Dcreate(ids[0], "ES_RealLW_900-905_BT",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    //ids[2] = H5.H5Dcreate(ids[0], "ES_ImaginaryLW",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    ids[2] = H5.H5Dcreate(ids[0], "ES_RealMW_1598-1602_BT",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    //ids[4] = H5.H5Dcreate(ids[0], "ES_ImaginaryMW",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    ids[3] = H5.H5Dcreate(ids[0], "ES_RealSW_2425-2430_BT",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    //ids[6] = H5.H5Dcreate(ids[0], "ES_ImaginarySW",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    ids[4] = H5.H5Dcreate(ids[0], "Latitude_LW",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    ids[5] = H5.H5Dcreate(ids[0], "Longitude_LW",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    ids[6] = H5.H5Dcreate(ids[0], "Latitude_MW",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    ids[7] = H5.H5Dcreate(ids[0], "Longitude_MW",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    ids[8] = H5.H5Dcreate(ids[0], "Latitude_SW",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    ids[9] = H5.H5Dcreate(ids[0], "Longitude_SW",HDF5Constants.H5T_NATIVE_FLOAT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    //ids[10] = H5.H5Dcreate(ids[0], "QF4_LW",HDF5Constants.H5T_NATIVE_INT, dataspace_id,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			    H5.H5Sclose(dataspace_id);
				}
		}
				catch (Exception e) {
					e.printStackTrace();
				}
		return ids;
	}
	
	private static void closeOutputFile(int[] ids){
		try {
			    for(int i=1;i<ids.length;i++)
					if (ids[i] >= 0)
						H5.H5Dclose(ids[i]);			    
				if (ids[0] >= 0)
					H5.H5Fclose(ids[0]);
			    
				}
				catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	private static void closeInputFiles(int[] ids) {
		try {
			for(int i=2;i<ids.length;i++)	
			{
				H5.H5Dclose(ids[i]);
				//System.out.println("closed "+i);
			}
			H5.H5Fclose(ids[0]);
			H5.H5Fclose(ids[1]);
		} catch (Exception e) {
		}		
		
	}
	

	
	private static void writeRow(int dataset_id,float [][] TwoDdata,int nRows, int nCols, int startrow) {
		long[] dims=new long[2];
		//dims[0]=(long)nRows;dims[1]=(long)nCols;
		
		//System.out.println("in writeRow");
		//for(int i=0;i<TwoDdata.length;i++)
		//	for(int j=0;j<TwoDdata[0].length;j++)
		//		System.out.println(i+","+j+"->"+TwoDdata[i][j]);
	
		try{
			dims=PrepareCris.readDimensions(dataset_id);
			// Define and select the first part of the hyperslab selection.
			//System.out.println("nRows="+nRows+" nCols="+nCols+" startrow="+startrow+"Twodata dim"+TwoDdata.length+","+TwoDdata[0].length);
			long[] start = { startrow, 0 };
			long[] stride = { 1, 1 };
			long[] count = { nRows, nCols };
			long[] block = { 1, 1 };
			int filespace_id = H5.H5Screate_simple(dims.length, dims, null);
			if ((filespace_id >= 0))
					H5.H5Sselect_hyperslab(filespace_id, HDF5Constants.H5S_SELECT_SET,start, stride, count, block);
			/*Define the memory dataspace.*/      
	        int memspace = H5.H5Screate_simple(2,count,count);   
		       /* Define memory hyperslab.*/
	           start[0]=0;
		       H5.H5Sselect_hyperslab(memspace, HDF5Constants.H5S_SELECT_SET, start, stride, count, block);
			if (dataset_id >= 0)
				H5.H5Dwrite(dataset_id, HDF5Constants.H5T_NATIVE_FLOAT,memspace, filespace_id, HDF5Constants.H5P_DEFAULT,TwoDdata);
		    }
			catch (Exception e) {
				e.printStackTrace();
			}
		//System.out.println("finished writeRow");
		
	}
	
	private static void writeintRow(int dataset_id,int [][] TwoDdata,int nRows, int nCols, int startrow) {
		long[] dims=new long[2];
		//dims[0]=(long)nRows;dims[1]=(long)nCols;
		
		//System.out.println("in writeRow");
		//for(int i=0;i<TwoDdata.length;i++)
		//	for(int j=0;j<TwoDdata[0].length;j++)
		//		System.out.println(i+","+j+"->"+TwoDdata[i][j]);
	
		try{
			dims=PrepareCris.readDimensions(dataset_id);
			// Define and select the first part of the hyperslab selection.
			//System.out.println("nRows="+nRows+" nCols="+nCols+" startrow="+startrow+"Twodata dim"+TwoDdata.length+","+TwoDdata[0].length);
			long[] start = { startrow, 0 };
			long[] stride = { 1, 1 };
			long[] count = { nRows, nCols };
			long[] block = { 1, 1 };
			int filespace_id = H5.H5Screate_simple(dims.length, dims, null);
			if ((filespace_id >= 0))
					H5.H5Sselect_hyperslab(filespace_id, HDF5Constants.H5S_SELECT_SET,start, stride, count, block);
			/*Define the memory dataspace.*/      
	        int memspace = H5.H5Screate_simple(2,count,count);   
		       /* Define memory hyperslab.*/
	           start[0]=0;
		       H5.H5Sselect_hyperslab(memspace, HDF5Constants.H5S_SELECT_SET, start, stride, count, block);
			if (dataset_id >= 0)
				H5.H5Dwrite(dataset_id, HDF5Constants.H5T_NATIVE_INT,memspace, filespace_id, HDF5Constants.H5P_DEFAULT,TwoDdata);
		}
			catch (Exception e) {
				e.printStackTrace();
			}
		//System.out.println("finished writeRow");
		
	}
	
	private static float convertRad2BT(float radiance,float v) { //radiance in mW, v in per cm)
	    double h = 6.62606876E-34; // Planck's constant in Js
		double c = 2.99792458E8;   // Velocity of light (m/s)
		double K = 1.3806503E-23;  // Boltzmann's constant (J/deg)
		double pi = 3.14159265359;

		//double c1=2*h*c*1E9; 
		double c1 = 2*h*c*c*1E11; //use this
		double c2= h*c*100/K;
		

		double BT=c2 * v / (Math.log(1 + c1 * Math.pow(v,3) / radiance));
		if(BT<100.0) BT=-999.0;
		if(BT>400) BT=-999.0;
		//System.out.println("Value in:"+radiance+",v="+v+" Value out:"+BT);
		return (float)BT;
	}
	
	private static float[] populate_channel_wavenumber(double startv,double incrementv,int nchannels){
		float[] v=new float[nchannels];
		for (int i=0;i<nchannels;i++)
			v[i]=(float) (startv+i*incrementv);
		return v;
		
	}
	

}