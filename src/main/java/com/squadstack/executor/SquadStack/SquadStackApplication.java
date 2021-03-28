package com.squadstack.executor.SquadStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.squadstack.executor.abstraction.DBConnection;
import com.squadstack.executor.abstraction.GenericStatus;


@SpringBootApplication
@EnableScheduling
@ComponentScan({"com.squadstack.executor"})
public class SquadStackApplication {

	@Autowired
	@Qualifier("genericStatusImpl")
	GenericStatus genericStatus;
	
	@Autowired
	@Qualifier("derbyConnection")
	DBConnection ConnectionPool;
	Connection conn = null;
	
	public static void main(String[] args){
		ApplicationContext ctx = SpringApplication.run(SquadStackApplication.class, args);
		SquadStackApplication Obj  =  ctx.getBean(SquadStackApplication.class);
		Obj.process();
	}
	
	public void process() {
		FileReader fr = null;
		try {
			File file = new File("C:\\Users\\rkumar14\\Desktop\\logfiles\\InputFile.txt"); // creates a new file instance
			fr = new FileReader(file); // reads the file
			BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
			StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
			String line;
			
			conn = ConnectionPool.getConnection();
			while ((line = br.readLine()) != null) {
				genericStatus.parkingLotstatusGeneric(line,conn);
			}
			
		}catch(IOException e){
			System.out.println("Exception :: "+ e);
		}catch(SQLException e){
			System.out.println("Exception :: "+ e);
		}finally {
			if(fr!=null && ConnectionPool!=null)
				try {
					fr.close();// closes the stream and release the resources
					genericStatus.dropInMemoryTable("parking_slot", conn); //  drop in memory table before closing connection
					ConnectionPool.closeConnection(conn); // closing connection
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Exception while closing fileReader :: "+ e);
				} catch(SQLException e){
					System.out.println("Exception while closing connection :: "+ e);
				}
		}
	}
	
	
	// If you need to schedule the process after specific interval of itme then we can run that way as well
	/*@Scheduled(fixedRate = 1000)
	public void scheduleFixedRateTask() {
	   process();
	}*/

}
