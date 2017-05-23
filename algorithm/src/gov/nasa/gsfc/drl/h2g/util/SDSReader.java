/******************************************************************************
Copyright © 1999-2007, United States Government as represented by the Administrator for The National Aeronautics and Space Administration.  All Rights Reserved.
*******************************************************************************/


package gov.nasa.gsfc.drl.h2g.util;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h4.H4File;
import ncsa.hdf.object.h4.H4SDS;
import ncsa.hdf.hdf5lib.*;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;

public class SDSReader {
	private H4File h4;
	private H4SDS sds;
	private Datatype sdsDatatype;
	private long[] dims;
	private long[] selectedDims;
	private int[] selectedIndex;
	private long[] startDims;
	private long[] stride;
	private static final int HEIGHT = 0;
	private static final int WIDTH = 1;
	private static final int DEPTH = 2;
	public static final int NO_DEPTH = -1;
	private int H5file_id=-1,H5dataset_id=-1;
	private long[] H5max_dims;
	private int H5Datatype;
	private int H5rank;
	private int H5dataspace;

	/****************************************************************************
	 * SDSReader.
	 ****************************************************************************/
	public SDSReader(File file, String[] path, int[] indexSelections)
			throws Exception {
		//System.out.println(">> Initialised sds 0");
		//System.out.println(file.getAbsolutePath());
		h4 = new H4File(file.getAbsolutePath(), FileFormat.READ);
		//System.out.println(file.getAbsolutePath());
		//System.out.println(">> Initialised sds 1");
		h4.open();
		//System.out.println(">> Initialised sds 2");
		sds = getThumbHObject(h4.getRootNode(), path);

		//System.out.println(">> Initialised sds 3");

		// sds=new H4SDS(FileFormat fileFormat,java.lang.String
		// name,java.lang.String path,long[] oid)

		sds.init();
		sdsDatatype = sds.getDatatype();
		dims = sds.getDims();
		if (dims.length < 2) {
			throw new Exception("less than 2 dimensions not supported");
		}
		if (dims.length > 3) {
			throw new Exception("greater than 3 dimensions not supported");
		}
		selectedDims = sds.getSelectedDims();
		selectedIndex = sds.getSelectedIndex();
		// System.out.println("Selected Index:"+selectedIndex[0]+" "+selectedIndex[1]);
		startDims = sds.getStartDims();
		stride = sds.getStride();
		selectedIndex[HEIGHT] = indexSelections[HEIGHT];
		selectedIndex[WIDTH] = indexSelections[WIDTH];
		if (indexSelections.length > 2) {
			selectedIndex[DEPTH] = indexSelections[DEPTH];
		}

	}
	
	/****************************************************************************
	 * SDSReader.for HDF5 files
	 * @param indexSelections 
	 * @param H5Datatype 
	 * @param H5rank 
	 ****************************************************************************/
	
	
	public SDSReader(File file, String path, int[] indexSelections)
	throws Exception {
		int status_n;
		System.out.println("File: "+file+" Dataset: "+path);
		try {
			H5file_id = H5.H5Fopen(file.toString(), HDF5Constants.H5F_ACC_RDONLY,HDF5Constants.H5P_DEFAULT);
			//System.out.println("1");
			if (H5file_id >= 0){
				//H5dataset_id = H5.H5Dopen(H5file_id, path);
				H5dataset_id = H5.H5Dopen(H5file_id, path,HDF5Constants.H5P_DATASET_ACCESS_DEFAULT );
				//System.out.println("dataset id="+H5dataset_id);
				//System.out.println("dapl="+HDF5Constants.H5P_DATASET_ACCESS_DEFAULT);
			}
			    
		}
		catch (Exception e) {
			if(path.equals("/All_Data/VIIRS-VI_EDR_All/TOA_NDVI") )
			{
				path="/All_Data/VIIRS-VI-EDR_All/TOA_NDVI";
				H5dataset_id = H5.H5Dopen(H5file_id, path,HDF5Constants.H5P_DATASET_ACCESS_DEFAULT );
			}
			else if (path.equals("/All_Data/VIIRS-VI_EDR_All/TOC_EVI") )
			{
				path="/All_Data/VIIRS-VI-EDR_All/TOC_EVI";
				H5dataset_id = H5.H5Dopen(H5file_id, path,HDF5Constants.H5P_DATASET_ACCESS_DEFAULT );
			}
			else
			   e.printStackTrace();
		}
		H5Datatype=H5.H5Dget_type(H5dataset_id);



		H5dataspace = H5.H5Dget_space(H5dataset_id);    /* dataspace handle */
		H5rank      = H5.H5Sget_simple_extent_ndims(H5dataspace);

		dims=new long[H5rank];
		H5max_dims=new long[H5rank];
		status_n  = H5.H5Sget_simple_extent_dims(H5dataspace, dims, H5max_dims);
		selectedIndex=new int[H5rank];
		selectedIndex[HEIGHT] = indexSelections[HEIGHT];;
		selectedIndex[WIDTH] = indexSelections[WIDTH];;
		if (H5rank > 2) {
			selectedIndex[DEPTH] = indexSelections[DEPTH];
		}



	}

	
	public SDSReader(int H5file_id, String path, int[] indexSelections)
	throws Exception {
		int status_n;
		this.H5file_id=H5file_id;
		try {
			if (H5file_id >= 0)
				//H5dataset_id = H5.H5Dopen(H5file_id, path);
			    H5dataset_id = H5.H5Dopen(H5file_id, path,HDF5Constants.H5P_DATASET_ACCESS_DEFAULT );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		H5Datatype=H5.H5Dget_type(H5dataset_id);



		H5dataspace = H5.H5Dget_space(H5dataset_id);    /* dataspace handle */
		H5rank      = H5.H5Sget_simple_extent_ndims(H5dataspace);

		dims=new long[H5rank];
		H5max_dims=new long[H5rank];
		status_n  = H5.H5Sget_simple_extent_dims(H5dataspace, dims, H5max_dims);
		selectedIndex=new int[H5rank];
		selectedIndex[HEIGHT] = indexSelections[HEIGHT];;
		selectedIndex[WIDTH] = indexSelections[WIDTH];;
		if (H5rank > 2) {
			selectedIndex[DEPTH] = indexSelections[DEPTH];
		}



	}


	/****************************************************************************
	 * SDSReader.
	 ****************************************************************************/
	public SDSReader(H4File h4, String[] path, int[] indexSelections)
			throws Exception {

		sds = getThumbHObject(h4.getRootNode(), path);

		
		sds.init();
		sdsDatatype = sds.getDatatype();
		dims = sds.getDims();
		if (dims.length < 2) {
			throw new Exception("less than 2 dimensions not supported");
		}
		if (dims.length > 3) {
			throw new Exception("greater than 3 dimensions not supported");
		}
		selectedDims = sds.getSelectedDims();
		selectedIndex = sds.getSelectedIndex();
		// System.out.println("Selected Index:"+selectedIndex[0]+" "+selectedIndex[1]);
		startDims = sds.getStartDims();
		stride = sds.getStride();
		selectedIndex[HEIGHT] = indexSelections[HEIGHT];
		selectedIndex[WIDTH] = indexSelections[WIDTH];
		if (indexSelections.length > 2) {
			selectedIndex[DEPTH] = indexSelections[DEPTH];
		}

	}

	/****************************************************************************
	 * getRank.
	 ****************************************************************************/
	public int getRank() {
		return dims.length;
	}

	/****************************************************************************
	 * getHeight.
	 ****************************************************************************/
	public int getHeight() {
		return (int) dims[selectedIndex[HEIGHT]];
	}

	/****************************************************************************
	 * getWidth.
	 ****************************************************************************/
	public int getWidth() {
		return (int) dims[selectedIndex[WIDTH]];
	}

	/****************************************************************************
	 * getDepth.
	 ****************************************************************************/
	public int getDepth() {
		if (dims.length < 3) {
			return NO_DEPTH;
		}
		return (int) dims[selectedIndex[DEPTH]];
	}

	/****************************************************************************
	 * getDouble.
	 ****************************************************************************/
	public double getDouble(int row, int col) throws Exception {
		return getDouble(row, col, NO_DEPTH);
	}

	public double getDouble(int row, int col, int depth) throws Exception {
		Object dataValues = getData(row, col, 1, 1, 1, 1, depth);
		return Array.getDouble(dataValues, 0);
	}

	/****************************************************************************
	 * getDoubleRow.
	 ****************************************************************************/
	public double[] getDoubleRow(int row, int col, int count, int depth)
			throws Exception {
		Object dataValues = getData(row, col, 1, count, 1, 1, depth);
		double[] doubles = new double[count];
		for (int i = 0; i < count; i++) {
			doubles[i] = Array.getDouble(dataValues, i);
		}
		return doubles;
	}

	public double[] getDoubleRow(int row, double[] buffer, int depth)
			throws Exception {
		Object dataValues = getData(row, 0, 1, buffer.length, 1, 1, depth);
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = Array.getDouble(dataValues, i);
		}
		return buffer;
	}

	/****************************************************************************
	 * getDoubleCol.
	 ****************************************************************************/
	public double[] getDoubleCol(int col, int row, int count, int depth)
			throws Exception {
		Object dataValues = getData(row, col, count, 1, 1, 1, depth);
		double[] doubles = new double[count];
		for (int i = 0; i < count; i++) {
			doubles[i] = Array.getDouble(dataValues, i);
		}
		return doubles;
	}

	/****************************************************************************
	 * getDoubles.
	 ****************************************************************************/
	public double[][] getDoubles(int row, int col, int rows, int cols,
			int rowStride, int colStride) throws Exception {
		return getDoubles(row, col, rows, cols, rowStride, colStride, NO_DEPTH);
	}

	public double[][] getDoubles(int row, int col, int rows, int cols,
			int rowStride, int colStride, int depth) throws Exception {
		Object dataValues = getData(row, col, rows, cols, rowStride, colStride,
				depth);
		double[][] doubles = new double[rows][cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				doubles[r][c] = Array.getDouble(dataValues, (r * cols) + c);
			}
		}
		return doubles;
	}

	/****************************************************************************
	 * getInt.
	 ****************************************************************************/
	public int getInt(int row, int col) throws Exception {
		return getInt(row, col, NO_DEPTH);
	}

	public int getInt(int row, int col, int depth) throws Exception {
		Object dataValues = getData(row, col, 1, 1, 1, 1, depth);
		return fixInt(Array.getInt(dataValues, 0), sdsDatatype
				.getDatatypeSize(), sdsDatatype.isUnsigned());
	}

	/****************************************************************************
	 * getIntRow.
	 ****************************************************************************/
	public int[] getIntRow(int row, int col, int stride, int depth, int[] buffer)
			throws Exception {
		Object dataValues = getData(row, col, 1, buffer.length, 1, stride,
				depth);
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = fixInt(Array.getInt(dataValues, i), sdsDatatype
					.getDatatypeSize(), sdsDatatype.isUnsigned());
		}
		return buffer;
	}

	public int[] getIntRow(int row, int col, int count, int depth)
			throws Exception {
		Object dataValues = getData(row, col, 1, count, 1, 1, depth);
		int[] ints = new int[count];
		for (int i = 0; i < count; i++) {
			ints[i] = fixInt(Array.getInt(dataValues, i), sdsDatatype
					.getDatatypeSize(), sdsDatatype.isUnsigned());
		}
		return ints;
	}

	/****************************************************************************
	 * getInts.
	 ****************************************************************************/
	public int[][] getInts(int row, int col, int rows, int cols, int rowStride,
			int colStride) throws Exception {
		return getInts(row, col, rows, cols, rowStride, colStride, NO_DEPTH);
	}
	
	/*public int[][] getHDF5Ints(int startrowSDS, int startcolSDS, int rowsSDS,
			int colsSDS, int rowStride, int colStride) throws NullPointerException, IllegalArgumentException, HDF5Exception {
		
		int[][] ints = new int[rowsSDS][colsSDS];		
		short[][] dset_data_short = new short[rowsSDS][colsSDS];
		byte[][] dset_data_byte = new byte[rowsSDS][colsSDS];
		
		
		System.out.println("In HDF5 read "+startrowSDS+","+startcolSDS+","+rowsSDS+","+colsSDS+","+rowStride+","+colStride);
		
		//Define hyperslab in the dataset
	    long offset[]=new long[2];
	    offset[0] = startrowSDS; offset[1] = startcolSDS;
	    long count[]=new long[2];
	    count[0]  = rowsSDS;  count[1] = colsSDS;
	    long stride[]=new long[2];
	    stride[0]=1;   stride[1]=1;
	    long block[]=new long[2];
	    block[0]=1;   block[1]=1;
	    int status = H5.H5Sselect_hyperslab(H5dataspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);
	    
	    
	    

	    //Define the memory dataspace.
	    long dimsm[]=new long[2];
	    dimsm[0] = rowsSDS;    dimsm[1] = colsSDS;
	    long dimsm_max[]=new long[2];
	    dimsm_max[0] = rowsSDS;  dimsm_max[1] = colsSDS;
	    int RANK_OUT=2;	    
	    int memspace = H5.H5Screate_simple(RANK_OUT,dimsm,dimsm_max);   
	    // Define memory hyperslab.
	    offset[0] = 0; offset[1] = 0;
	    status = H5.H5Sselect_hyperslab(memspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);
		if (H5dataset_id >= 0)
		{
			if (H5.H5Tequal(H5Datatype, HDF5Constants.H5T_NATIVE_UCHAR) || H5.H5Tequal(H5Datatype, HDF5Constants.H5T_STD_U8BE))
			{
				H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_UCHAR, memspace, H5dataspace,	HDF5Constants.H5P_DEFAULT, dset_data_byte);
				for(int i=0;i<rowsSDS;i++)
					for(int j=0;j<colsSDS;j++)
					{
						 	ints[i][j]=dset_data_byte[i][j] & 0xff;
					    
					   
					}
			}
			else if (H5.H5Tequal(H5Datatype, HDF5Constants.H5T_NATIVE_SHORT) || H5.H5Tequal(H5Datatype, HDF5Constants.H5T_STD_I16BE))
			{
				H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_SHORT, memspace, H5dataspace,	HDF5Constants.H5P_DEFAULT, dset_data_short);
				for(int i=0;i<rowsSDS;i++)
					for(int j=0;j<colsSDS;j++)
					{
					     ints[i][j]=dset_data_short[i][j] & 0xffff;					     
					}
				
			}
                        else if (H5.H5Tequal(H5Datatype, HDF5Constants.H5T_NATIVE_UINT16) || H5.H5Tequal(H5Datatype, HDF5Constants.H5T_STD_U16BE))
			{
				H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_UINT16, memspace, H5dataspace,HDF5Constants.H5P_DEFAULT, dset_data_short);
				for(int i=0;i<rowsSDS;i++)
					for(int j=0;j<colsSDS;j++)
					{
					     ints[i][j]=dset_data_short[i][j] & 0xffff;					     
					}
				
			}							
			else
			{
				System.out.println("No support for reading this datatype");
				System.exit(-1);
			}
			
		}
		
		return ints;
	}*/
	
	public int[][] getHDF5Ints(int startrowSDS, int startcolSDS, int rowsSDS,
			int colsSDS, int rowStride, int colStride) throws NullPointerException, IllegalArgumentException, HDF5Exception {
	   return getHDF5Ints(startrowSDS,startcolSDS,rowsSDS,colsSDS, rowStride, colStride, NO_DEPTH);
	}
	
	public int[][] getHDF5Ints(int startrowSDS, int startcolSDS, int rowsSDS,
			int colsSDS, int rowStride, int colStride, int depth) throws NullPointerException, IllegalArgumentException, HDF5Exception {
		
		int[][] ints = new int[rowsSDS][colsSDS];		
		short[][] dset_data_short = new short[rowsSDS][colsSDS];
		byte[][] dset_data_byte = new byte[rowsSDS][colsSDS];
		
		
		//System.out.println("In HDF5 read "+startrowSDS+","+startcolSDS+","+rowsSDS+","+colsSDS+","+rowStride+","+colStride+","+depth);
		
		/*Define hyperslab in the dataset*/
	    long offset[]=new long[selectedIndex.length];
	    offset[HEIGHT] = startrowSDS; offset[WIDTH] = startcolSDS;
	    if (depth!=NO_DEPTH) 
	    	offset[DEPTH]=depth;
	    long count[]=new long[selectedIndex.length];
	    count[HEIGHT]  = rowsSDS;  count[WIDTH] = colsSDS;
	    if (depth!=NO_DEPTH) 
	    	count[DEPTH]=1;
	    long stride[]=new long[selectedIndex.length];
	    stride[HEIGHT]=rowStride;   stride[WIDTH]=colStride;
	    if (depth!=NO_DEPTH) 
		    stride[DEPTH]=1;
	    long block[]=new long[selectedIndex.length];
	    block[HEIGHT]=1;   block[WIDTH]=1;
	    if (depth!=NO_DEPTH) 
			block[DEPTH]=1;
	    int status = H5.H5Sselect_hyperslab(H5dataspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);
	    
	    
	    

	    /*Define the memory dataspace.*/
	    long dimsm[]=new long[2];
	    dimsm[0] = rowsSDS;    dimsm[1] = colsSDS;
	    long dimsm_max[]=new long[2];
	    dimsm_max[0] = rowsSDS;  dimsm_max[1] = colsSDS;
	    int RANK_OUT=2;	    
	    int memspace = H5.H5Screate_simple(RANK_OUT,dimsm,dimsm_max);   
	    /* Define memory hyperslab.*/
	    offset[0] = 0; offset[1] = 0;
	    status = H5.H5Sselect_hyperslab(memspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);
		if (H5dataset_id >= 0)
		{
			if (H5.H5Tequal(H5Datatype, HDF5Constants.H5T_NATIVE_UCHAR) || H5.H5Tequal(H5Datatype, HDF5Constants.H5T_STD_U8BE))
			{
				H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_UCHAR, memspace, H5dataspace,	HDF5Constants.H5P_DEFAULT, dset_data_byte);
				for(int i=0;i<rowsSDS;i++)
					for(int j=0;j<colsSDS;j++)
					{
						 	ints[i][j]=dset_data_byte[i][j] & 0xff;
					    
					   
					}
			}
			else if (H5.H5Tequal(H5Datatype, HDF5Constants.H5T_NATIVE_SHORT) || H5.H5Tequal(H5Datatype, HDF5Constants.H5T_STD_I16BE))
			{
				H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_SHORT, memspace, H5dataspace,	HDF5Constants.H5P_DEFAULT, dset_data_short);
				for(int i=0;i<rowsSDS;i++)
					for(int j=0;j<colsSDS;j++)
					{
					     ints[i][j]=dset_data_short[i][j] & 0xffff;					     
					}
				
			}
                        else if (H5.H5Tequal(H5Datatype, HDF5Constants.H5T_NATIVE_UINT16) || H5.H5Tequal(H5Datatype, HDF5Constants.H5T_STD_U16BE))
			{
				H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_UINT16, memspace, H5dataspace,HDF5Constants.H5P_DEFAULT, dset_data_short);
				for(int i=0;i<rowsSDS;i++)
					for(int j=0;j<colsSDS;j++)
					{
					     ints[i][j]=dset_data_short[i][j] & 0xffff;					     
					}
				
			}							
			else
			{
				System.out.println("No support for reading this datatype");
				System.exit(-1);
			}
			
		}
		
		return ints;
	}
	public double[][] getHDF5Doubles(int startrowSDS, int startcolSDS,
			int rowsSDS, int colsSDS, int rowStride, int colStride) throws HDF5LibraryException, NullPointerException, HDF5Exception {
		return getHDF5Doubles(startrowSDS,startcolSDS,rowsSDS,colsSDS, rowStride, colStride, NO_DEPTH);
	}

	
	public double[][] getHDF5Doubles(int startrowSDS, int startcolSDS,
			int rowsSDS, int colsSDS, int rowStride, int colStride,int depth) throws HDF5LibraryException, NullPointerException, HDF5Exception {
		
		double[][] doubles = new double[rowsSDS][colsSDS];
		float[][] dset_data = new float[rowsSDS][colsSDS];
		
		
	//System.out.println("In HDF5 read "+startrowSDS+","+startcolSDS+","+rowsSDS+","+colsSDS+","+rowStride+","+colStride+","+depth);
		
		/*Define hyperslab in the dataset*/
	    long offset[]=new long[selectedIndex.length];
	    offset[HEIGHT] = startrowSDS; offset[WIDTH] = startcolSDS;
	    if (depth!=NO_DEPTH) 
	    	offset[DEPTH]=depth;
	    long count[]=new long[selectedIndex.length];
	    count[HEIGHT]  = rowsSDS;  count[WIDTH] = colsSDS;
	    if (depth!=NO_DEPTH) 
	    	count[DEPTH]=1;
	    long stride[]=new long[selectedIndex.length];
	    stride[HEIGHT]=rowStride;   stride[WIDTH]=colStride;
	    if (depth!=NO_DEPTH) 
		    stride[DEPTH]=1;
	    long block[]=new long[selectedIndex.length];
	    block[HEIGHT]=1;   block[WIDTH]=1;
	    if (depth!=NO_DEPTH) 
			block[DEPTH]=1;
	    int status = H5.H5Sselect_hyperslab(H5dataspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);
	    
	    
	    /*Define the memory dataspace.*/
	    long dimsm[]=new long[2];
	    dimsm[0] = rowsSDS;    dimsm[1] = colsSDS;
	    long dimsm_max[]=new long[2];
	    dimsm_max[0] = rowsSDS;  dimsm_max[1] = colsSDS;
	    int RANK_OUT=2;	    
	    int memspace = H5.H5Screate_simple(RANK_OUT,dimsm,dimsm_max);   
	    /* Define memory hyperslab.*/
	    offset[0] = 0; offset[1] = 0;
	    status = H5.H5Sselect_hyperslab(memspace, HDF5Constants.H5S_SELECT_SET, offset, stride, count, block);
		if (H5dataset_id >= 0)
				H5.H5Dread(H5dataset_id, HDF5Constants.H5T_NATIVE_FLOAT, memspace, H5dataspace,	HDF5Constants.H5P_DEFAULT, dset_data);	
		//Copy short to ints
		for(int i=0;i<rowsSDS;i++)
			for(int j=0;j<colsSDS;j++)
				doubles[i][j]=dset_data[i][j];
		return doubles;
	}

	public int[][] getInts(int row, int col, int rows, int cols, int rowStride,
			int colStride, int depth) throws Exception {
		Object dataValues = getData(row, col, rows, cols, rowStride, colStride,
				depth);
		int[][] ints = new int[rows][cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				ints[r][c] = fixInt(Array.getInt(dataValues, (r * cols) + c),
						sdsDatatype.getDatatypeSize(), sdsDatatype.isUnsigned());
			}
		}
		return ints;
	}

	/****************************************************************************
	 * getData.
	 ****************************************************************************/
	private Object getData(int row, int col, int rows, int cols, int rowStride,
			int colStride, int depth) throws Exception {
		startDims[selectedIndex[HEIGHT]] = row;
		startDims[selectedIndex[WIDTH]] = col;
		if (depth != NO_DEPTH) {
			startDims[selectedIndex[DEPTH]] = depth;
		}
		selectedDims[selectedIndex[HEIGHT]] = rows;
		selectedDims[selectedIndex[WIDTH]] = cols;
		if (depth != NO_DEPTH) {
			selectedDims[selectedIndex[DEPTH]] = 1;
		}
		stride[selectedIndex[HEIGHT]] = rowStride;
		stride[selectedIndex[WIDTH]] = colStride;
		if (depth != NO_DEPTH) {
			stride[selectedIndex[DEPTH]] = 1;
		}
		sds.clearData();
		sds.getData();
		sds.convertFromUnsignedC();
		// selectedIndex = sds.getSelectedIndex();
		// System.out.println("in get data Selected Index:"+selectedIndex[0]+" "+selectedIndex[1]);
		Object dataValues = sds.getData();
		return dataValues;
	}

	/****************************************************************************
	 * getIntegerFillValue.
	 ****************************************************************************/
	public Integer getIntegerFillValue() throws Exception {
		java.util.List attributes = sds.getMetadata();
		if (attributes != null) {
			Iterator it = attributes.iterator();
			while (it.hasNext()) {
				Attribute attr = (Attribute) it.next();
				if (attr.getName().equals("_FillValue")) {
					Datatype datatype = attr.getType();
					Object values = attr.getValue();
					return new Integer(fixInt(Array.getInt(values, 0), datatype
							.getDatatypeSize(), datatype.isUnsigned()));
				}
			}
		}
		return null;
	}

	/****************************************************************************
	 * getDoubleFileValue.
	 ****************************************************************************/
	public Double getDoubleFillValue() throws Exception {
		java.util.List attributes = sds.getMetadata();
		if (attributes != null) {
			Iterator it = attributes.iterator();
			while (it.hasNext()) {
				Attribute attr = (Attribute) it.next();
				if (attr.getName().equals("_FillValue")) {
					Object values = attr.getValue();
					return new Double(Array.getDouble(values, 0));
				}
			}
		}
		return null;
	}

	/****************************************************************************
	 * getIntValueRange.
	 ****************************************************************************/
	public int[] getIntValidRange() throws Exception {
		java.util.List attributes = sds.getMetadata();
		if (attributes != null) {
			Iterator it = attributes.iterator();
			while (it.hasNext()) {
				Attribute attr = (Attribute) it.next();
				if (attr.getName().equals("valid_range")) {
					Datatype datatype = attr.getType();
					Object values = attr.getValue();
					return new int[] {
							fixInt(Array.getInt(values, 0), datatype
									.getDatatypeSize(), datatype.isUnsigned()),
							fixInt(Array.getInt(values, 1), datatype
									.getDatatypeSize(), datatype.isUnsigned()) };
				}
			}
		}
		switch (sdsDatatype.getDatatypeClass()) {
		case Datatype.CLASS_INTEGER: {
			switch (sdsDatatype.getDatatypeSize()) {
			case 1: {
				if (sdsDatatype.isUnsigned()) {
					return new int[] { 0, 255 };
				}
				return new int[] { -128, 127 };
			}
			case 2: {
				if (sdsDatatype.isUnsigned()) {
					return new int[] { 0, 65535 };
				}
				return new int[] { -32768, 32767 };
			}
			}
		}
		}
		throw new Exception("unexpected datatype");
	}

	/****************************************************************************
	 * fixInt.
	 ****************************************************************************/
	private int fixInt(int value, int size, boolean unsigned) throws Exception {
		if (unsigned) {
			if (value < 0) {
				switch (size) {
				case 1: {
					value += 256;
					break;
				}
				case 2: {
					value += 65536;
					break;
				}
				default: {
					throw new Exception("unsupported unsigned integer size: "
							+ size);
				}
				}
			}
		}
		return value;
	}

	public int[][] doubleResolution(int[][] data, int mindata, int maxdata,
			int fillvalue) {
		int[][] highResData;
		int jLowResPrev;
		int iHighRes, jHighRes, iHighResNext, jHighResNext;
		int rowsLowRes = data.length;
		int colsLowRes = data[0].length;
		int rowsHighRes = data.length * 2;
		int colsHighRes = data[0].length * 2;
		highResData = new int[rowsHighRes][colsHighRes];
		highResData = new int[rowsHighRes][colsHighRes];
		// System.out.println("Start of double resolution");

		for (int iLowRes = 0; iLowRes < rowsLowRes; iLowRes++) {
			for (int jLowRes = 0; jLowRes < colsLowRes; jLowRes++) {

				iHighRes = iLowRes * 2;
				iHighResNext = iHighRes + 1;
				jHighRes = jLowRes * 2;
				jHighResNext = jHighRes + 1;
				// System.out.println(iLowRes+" "+jLowRes+" "+iHighRes+" "+jHighRes+" "+data[iLowRes][jLowRes]);
				if (jLowRes - 1 < 0)
					jLowResPrev = jLowRes;
				else
					jLowResPrev = jLowRes - 1;
				if (Util.inRange(mindata, data[iLowRes][jLowRes], maxdata)
						&& Util.inRange(mindata, data[iLowRes][jLowResPrev],
								maxdata)) {
					highResData[iHighRes][jHighRes] = Math
							.round((data[iLowRes][jLowRes] + data[iLowRes][jLowResPrev]) / 2);// data[iLowRes][jLowRes];
					highResData[iHighResNext][jHighRes] = Math
							.round((data[iLowRes][jLowRes] + data[iLowRes][jLowResPrev]) / 2);// data[iLowRes][jLowRes];

				} else {
					highResData[iHighRes][jHighRes] = fillvalue;
					highResData[iHighResNext][jHighRes] = fillvalue;

				}
				highResData[iHighRes][jHighResNext] = data[iLowRes][jLowRes];
				highResData[iHighResNext][jHighResNext] = data[iLowRes][jLowRes];

			}
		}
		// System.out.println("End of double resolution");
		return (highResData);
	}

	public int[][] upscaleMask(int[][] data, int xSDS, int ySDS) {
		int xMask = data.length;
		int yMask = data[0].length;
		int MaskScaleFactorX = xSDS / xMask;
		int MaskScaleFactorY = ySDS / yMask;

		int[][] highResData;
		int rowsLowRes = data.length;
		int colsLowRes = data[0].length;
		int rowsHighRes = xSDS;
		int colsHighRes = ySDS;
		int iHighResStart, jHighResStart;
		highResData = new int[rowsHighRes][colsHighRes];
		for (int iHighRes = 0; iHighRes < rowsHighRes; iHighRes++)
			for (int jHighRes = 0; jHighRes < colsHighRes; jHighRes++)
				highResData[iHighRes][jHighRes] = -9999;

		System.out.println("Upscaling Mask to match data dimension");
		System.out.println("data dimension: " + xSDS + "x" + ySDS + " mask: "
				+ rowsLowRes + "x" + colsLowRes + " Scales: "
				+ MaskScaleFactorX + "x" + MaskScaleFactorY);
		for (int iLowRes = 0; iLowRes < rowsLowRes; iLowRes++)
			for (int jLowRes = 0; jLowRes < colsLowRes; jLowRes++) {
				iHighResStart = iLowRes * MaskScaleFactorX;
				jHighResStart = jLowRes * MaskScaleFactorY;
				for (int iHighRes = iHighResStart; iHighRes < iHighResStart
						+ MaskScaleFactorX; iHighRes++)
					for (int jHighRes = jHighResStart; jHighRes < jHighResStart
							+ MaskScaleFactorY; jHighRes++)
						highResData[iHighRes][jHighRes] = data[iLowRes][jLowRes];

			}
		return (highResData);
	}

	public int[][] downscaleDiscreteMask(int[][] data, int xSDS, int ySDS) {
		int xMask = data.length;
		int yMask = data[0].length;
		int MaskScaleFactorX = xMask / xSDS;
		int MaskScaleFactorY = yMask / ySDS;

		int[][] lowResData;
		int rowsLowRes = xSDS;
		int colsLowRes = ySDS;
		int rowsHighRes = data.length;
		int colsHighRes = data[0].length;
		int iHighResStart, jHighResStart;
		lowResData = new int[rowsLowRes][colsLowRes];
		System.out
				.println("Downscaling Mask(Discrete Type) to match data dimension");
		System.out.println("data dimension: " + xSDS + "x" + ySDS + " mask: "
				+ rowsHighRes + "x" + colsHighRes + " Scales: "
				+ MaskScaleFactorX + "x" + MaskScaleFactorY);

		for (int iLowRes = 0; iLowRes < rowsLowRes; iLowRes++)
			for (int jLowRes = 0; jLowRes < colsLowRes; jLowRes++)
				lowResData[iLowRes][jLowRes] = -9999;

		int[] lowResPixel = new int[MaskScaleFactorX * MaskScaleFactorY];

		// System.out.println("Going to loop");

		for (int iLowRes = 0; iLowRes < rowsLowRes; iLowRes++)
			for (int jLowRes = 0; jLowRes < colsLowRes; jLowRes++) {
				iHighResStart = iLowRes * MaskScaleFactorX;
				jHighResStart = jLowRes * MaskScaleFactorY;
				// System.out.println("rows:"+iHighResStart+"-"+(iHighResStart+MaskScaleFactorX)+" cols:"+jHighResStart+"-"+(jHighResStart+MaskScaleFactorY));
				int i = 0;
				for (int iHighRes = iHighResStart; iHighRes < iHighResStart
						+ MaskScaleFactorX; iHighRes++)
					for (int jHighRes = jHighResStart; jHighRes < jHighResStart
							+ MaskScaleFactorY; jHighRes++)
						lowResPixel[i++] = data[iHighRes][jHighRes];
				lowResData[iLowRes][jLowRes] = Util.mode(lowResPixel);

			}

		return (lowResData);

	}

	public int[][] downscaleContinuousMask(int[][] data, int xSDS, int ySDS) {
		int xMask = data.length;
		int yMask = data[0].length;
		int MaskScaleFactorX = xMask / xSDS;
		int MaskScaleFactorY = yMask / ySDS;

		int[][] lowResData;
		int rowsLowRes = xSDS;
		int colsLowRes = ySDS;
		int rowsHighRes = data.length;
		int colsHighRes = data[0].length;
		int iHighResStart, jHighResStart;
		lowResData = new int[rowsLowRes][colsLowRes];
		System.out
				.println("Downscaling Mask(Continuous Type) to match data dimension");
		System.out.println("data dimension: " + xSDS + "x" + ySDS + " mask: "
				+ rowsHighRes + "x" + colsHighRes + " Scales: "
				+ MaskScaleFactorX + "x" + MaskScaleFactorY);

		for (int iLowRes = 0; iLowRes < rowsLowRes; iLowRes++)
			for (int jLowRes = 0; jLowRes < colsLowRes; jLowRes++)
				lowResData[iLowRes][jLowRes] = -9999;

		int[] lowResPixel = new int[MaskScaleFactorX * MaskScaleFactorY];

		// System.out.println("Going to loop");

		for (int iLowRes = 0; iLowRes < rowsLowRes; iLowRes++)
			for (int jLowRes = 0; jLowRes < colsLowRes; jLowRes++) {
				iHighResStart = iLowRes * MaskScaleFactorX;
				jHighResStart = jLowRes * MaskScaleFactorY;
				// System.out.println("rows:"+iHighResStart+"-"+(iHighResStart+MaskScaleFactorX)+" cols:"+jHighResStart+"-"+(jHighResStart+MaskScaleFactorY));
				int i = 0;
				for (int iHighRes = iHighResStart; iHighRes < iHighResStart
						+ MaskScaleFactorX; iHighRes++)
					for (int jHighRes = jHighResStart; jHighRes < jHighResStart
							+ MaskScaleFactorY; jHighRes++)
						lowResPixel[i++] = data[iHighRes][jHighRes];
				lowResData[iLowRes][jLowRes] = Util.avg(lowResPixel);

			}

		return (lowResData);

	}

	public int[][] doubleMASKResolution(int[][] data, int mindata, int maxdata,
			int fillvalue) {
		// This function produces slight inexact values since MODIS 1km pixels
		// do not exactly overlap MODIS 500m pixels.
		// Function assumes each 1km pixel contains 4 500m pixels (This is not
		// exactly true; One 1km pixel contains 2 complete 500m pixels and 4
		// half 500m pixels
		int[][] highResData;
		int iHighRes, jHighRes, iHighResNext, jHighResNext;
		int rowsLowRes = data.length;
		int colsLowRes = data[0].length;
		int rowsHighRes = data.length * 2;
		int colsHighRes = data[0].length * 2;
		highResData = new int[rowsHighRes][colsHighRes];
		// highResData=new int[rowsHighRes][colsHighRes];
		System.out.println("Start of doubleMASKResolution");

		for (int iLowRes = 0; iLowRes < rowsLowRes; iLowRes++) {
			for (int jLowRes = 0; jLowRes < colsLowRes; jLowRes++) {

				iHighRes = iLowRes * 2;
				iHighResNext = iHighRes + 1;
				jHighRes = jLowRes * 2;
				jHighResNext = jHighRes + 1;

				highResData[iHighRes][jHighRes] = data[iLowRes][jLowRes];
				highResData[iHighResNext][jHighRes] = data[iLowRes][jLowRes];
				highResData[iHighRes][jHighResNext] = data[iLowRes][jLowRes];
				highResData[iHighResNext][jHighResNext] = data[iLowRes][jLowRes];

			}
		}
		System.out.println("End of doubleMASKResolution");
		return (highResData);
	}

	/****************************************************************************
	 * getThumbHObject.
	 ****************************************************************************/
	private H4SDS getThumbHObject(TreeNode root, String[] path)
			throws Exception {
		HObject ho = getHObject(root, path);
		if (ho == null) {
			throw new Exception("data object not found");
		}
		if (!(ho instanceof H4SDS)) {
			throw new Exception("HObject not an H4SDS");
		}
		return (H4SDS) ho;
	}

	/****************************************************************************
	 * getHObject.
	 ****************************************************************************/
	private HObject getHObject(TreeNode root, String[] path) throws Exception {
		// Next Line changed by S. Dasgupta 11/23/07 from
		// "LinkedList/*<String>*/ objectNames = new LinkedList/*<String>*/();"
		// Type checking for java 1.5
		LinkedList<String> objectNames = new LinkedList<String>();
		for (int i = 0; i < path.length; i++) {
			objectNames.add(path[i]);
		}
		return findHObject(root, objectNames);
	}

	/****************************************************************************
	 * findHObject.
	 ****************************************************************************/
	private HObject findHObject(TreeNode baseNode,
			LinkedList/* <String> */objectNames) throws Exception {
		String objectName = (String) objectNames.removeFirst();
		int count = baseNode.getChildCount();
		for (int i = 0; i < count; i++) {
			TreeNode node = baseNode.getChildAt(i);
			HObject ho = (HObject) ((DefaultMutableTreeNode) node)
					.getUserObject();
			if (ho.getName().equals(objectName)) {
				if (objectNames.size() > 0) {
					return findHObject(node, objectNames);
				} else {
					return ho;
				}
			}
		}
		return null;
	}

	/****************************************************************************
	 * close.
	 ****************************************************************************/
	public void close() {
		try {
			h4.close();
		} catch (Exception e) {
		}
	}

	public int[][] downscaleResolution(int[][] data, int min, int max, int fill_value, int stride) {
		
		int rowsLowRes = data.length/stride;
		int colsLowRes = data[0].length/stride;
		int[][] lowResData=new int[rowsLowRes][colsLowRes];

		System.out.println("Downscaling resolution");
		for(int i=0;i<rowsLowRes;i++)
			for(int j=0;j<colsLowRes;j++)			
				lowResData[i][j]=data[i*stride][j*stride];


			
		return lowResData;
	}

	public void h5close() {
		try {
			H5.H5Fclose(H5file_id);
			H5.H5Dclose(H5dataset_id);
		} catch (Exception e) {
		}		
		
	}

	

	
	

}
