package com.squadstack.executor.Services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.squadstack.executor.Constants.Constants;
import com.squadstack.executor.abstraction.GenericStatus;

@Service
public class GenericStatusImpl implements GenericStatus{

	
	PreparedStatement pstmt = null;
	ResultSet resultSet = null;
	boolean createSlotFlag  = true;
	private static int SLOT_SIZE = -1;
	@Override
	public void parkingLotstatusGeneric(String input,Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		
		int squence = (input.contains(Constants.CREATE_PARKING_LOT))?1:
			input.contains(Constants.PARK)?2:
			input.contains(Constants.SLOT_NUMBERS_FOR_DRIVER_OF_AGE)?3:
			input.contains(Constants.SLOT_NUMBER_FOR_CAR_WITH_NUMBER)?4:
			input.contains(Constants.LEAVE)?5:
			input.contains(Constants.VEHICLE_REGISTRATION_NUMBER_FOR_DRIVER_OF_AGE)?6:-1;
	
	
	 switch(squence) {
	 
	   case 1:
		   createSlots(input,conn);
		   break;
	   case 2:
		   park(input,conn);
		   break;
	   case 3:
		   slotNumberForDriver(input,conn);
		   break;
	   case 4:
		   slotNumberForCar(input,conn);
		   break;
	   case 5:
		   leave(input,conn);
		   break;
	   case 6:
		   vehicleRegistration(input,conn);
		   break;
	   default:
		   System.out.println("feature not supported");
	   }
	
	}
	
	
	private void createSlots(String input,Connection conn) {
		
		//creating table while creating slot and droping after last query
		
		String create = "create table parking_slot ("
			      + "id integer not null generated always as"
			      + " identity (start with 1, increment by 1),   "
			      + "registration_number varchar(100) not null, age integer,slot integer,"
			      + "constraint primary_key primary key (registration_number))";
		
		SLOT_SIZE = Integer.parseInt(input.split(" ")[1]);
		if(SLOT_SIZE==0) {
			System.out.println("parking slot should be greater than 1");
			return;
		}
			
		try {
			pstmt = conn.prepareStatement(create);
			pstmt.execute();
			createSlotFlag = true;
			pstmt.close();
			System.out.println("Created parking of "+SLOT_SIZE +" slots");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private void park(String input,Connection conn) {
		
    	if(!createSlotFlag) {
    		System.out.println("please create slots first");
    		return;
    	}
    	int size = getCount("parking_slot", conn);
    	
    	if(SLOT_SIZE<=size) {
    		System.out.println("slot is already full");
    		return;
    	}
    		
    	
    	String inputString[] =  input.split(" ");
    	int slotNumber = 1;
    	
    	
    	int missingSlot = getSmallestMissingNumber(conn);
    	if(missingSlot==-1) {
    		int maxSlot = getMaxSlot(conn);
    		if(maxSlot!=-1)
    			slotNumber = maxSlot+1;
    	}else {
    		slotNumber = missingSlot;
    	}
    	
    	String query = "insert into parking_slot (registration_number,age,slot) values ('"+ inputString[1]+"'," + Integer.parseInt(inputString[3]) + "," + slotNumber +")";
    	try {
			pstmt = conn.prepareStatement(query);
			pstmt.execute();
			pstmt.close();
			System.out.println("Car with vehicle registration number \"" + inputString[1]+ "\" has been parked at slot number "+  slotNumber);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }
    
    
    private int getCount(String tableName,Connection conn) {
    	String query = "select count(*) as size from "+tableName;
    	try {
			pstmt = conn.prepareStatement(query);
			resultSet = pstmt.executeQuery();
			int slot  = 0;
			while(resultSet.next()) {
				slot =  resultSet.getInt("size");
			}
			resultSet.close();
			pstmt.close();
			return slot;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return -1;
    }
    
    
    private void slotNumberForDriver(String input,Connection conn) {
    	
    	if(!createSlotFlag) {
    		System.out.println("please create slots first");
    		return;
    	}
    	
    	String query = "select slot from parking_slot where age = " + Integer.parseInt(input.split(" ")[1]);
    	try {
			pstmt = conn.prepareStatement(query);
			resultSet = pstmt.executeQuery();
			boolean flag = false;
			while(resultSet.next()) {
				if(flag==true)
					System.out.print(",");
				
				int slot =  resultSet.getInt("slot");
				System.out.print(slot);
				flag = true;
			}
			resultSet.close();
			pstmt.close();
			System.out.println();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void slotNumberForCar(String input,Connection conn) {
	 
    	if(!createSlotFlag) {
    		System.out.println("please create slots first");
    		return;
    	}
    	
    	String query = "select slot from parking_slot where registration_number = '" + input.split(" ")[1]+"'";
    	try {
			pstmt = conn.prepareStatement(query);
			resultSet = pstmt.executeQuery();
			while(resultSet.next()) {
				int slot =  resultSet.getInt("slot");
				System.out.println(slot);
			}
			resultSet.close();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private int getSmallestMissingNumber(Connection conn) {
    	String query = "select ps.slot + 1 as slots " + 
    			"from parking_slot as ps " + 
    			"  left outer join parking_slot as r on ps.slot + 1 = r.slot " + 
    			"where r.slot is null";
    	
    	try {
			pstmt = conn.prepareStatement(query);
			resultSet = pstmt.executeQuery();
			while(resultSet.next()) {
				int slots =  resultSet.getInt("slots");
				return slots;
			}
			resultSet.close();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return -1;
    }
    
    private int getMaxSlot(Connection conn) {
    	String query = "select max(slot) as slots from parking_slot";
    	
    	try {
			pstmt = conn.prepareStatement(query);
			resultSet = pstmt.executeQuery();
			while(resultSet.next()) {
				int slot =  resultSet.getInt("slots");
				return slot;
			}
			resultSet.close();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return -1;
    }
    
	private void leave(String input,Connection conn) {
		if(!createSlotFlag) {
    		System.out.println("please create slots first");
    		return;
    	}
		String rNumber  = null;
		int age  = -1;
		String selectQuery = "select * from parking_slot where slot = " + Integer.parseInt(input.split(" ")[1]);
		String query = "delete from parking_slot where slot = "+ Integer.parseInt(input.split(" ")[1]);
    	try {
    		pstmt = conn.prepareStatement(selectQuery);
    		ResultSet result = pstmt.executeQuery();
    		while(result.next()) {
    			rNumber = result.getString("registration_number");
    			age = result.getInt("age");
    		}
    		
    		if(age==-1) {
    			System.out.println("parking slot " +Integer.parseInt(input.split(" ")[1]) + " is empty");
    			return;
    		}
    			
    		
			pstmt = conn.prepareStatement(query);
			pstmt.execute();
			pstmt.close();
			System.out.println("Slot number "+ Integer.parseInt(input.split(" ")[1]) + ", the car with the vehicle registration number \""+ rNumber+ "\" left the space, the driver of the car was of age "+ age);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void vehicleRegistration(String input,Connection conn) {
		
		if(!createSlotFlag) {
    		System.out.println("please create slots first");
    		return;
    	}
		
		String query = "select registration_number from parking_slot where age = "+ Integer.parseInt(input.split(" ")[1]);
		try {
			pstmt = conn.prepareStatement(query);
			resultSet = pstmt.executeQuery();
			StringBuilder builder = new StringBuilder("");
			while(resultSet.next()) {
				builder.append(resultSet.getString("registration_number") + "  ");
			}
			resultSet.close();
			pstmt.close();
			if(builder.length()>2)
			   System.out.println("Vehicle registration number for driver of age " + Integer.parseInt(input.split(" ")[1]) + " are " + builder);
			else
				System.out.println("null");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


	@Override
	public void dropInMemoryTable(String dbName,Connection conn) throws SQLException {
		if(!createSlotFlag) {
    		System.out.println("please create slots first");
    		return;
    	}
		
		String dorpTable = "drop table " + dbName ;
		try {
			pstmt = conn.prepareStatement(dorpTable);
			pstmt.execute();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
