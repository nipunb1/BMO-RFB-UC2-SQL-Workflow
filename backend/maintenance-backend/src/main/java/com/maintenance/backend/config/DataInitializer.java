package com.maintenance.backend.config;

import com.maintenance.backend.model.User;
import com.maintenance.backend.model.MaintenanceRequest;
import com.maintenance.backend.service.UserService;
import com.maintenance.backend.service.MaintenanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MaintenanceRequestService requestService;
    
    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeSampleRequests();
    }
    
    private void initializeUsers() {
        if (userService.getAllUsers().isEmpty()) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@maintenance.com");
            admin.setPassword("admin123");
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(User.UserStatus.ACTIVE);
            userService.createUser(admin);
            
            User manager = new User();
            manager.setName("Manager Smith");
            manager.setEmail("manager@maintenance.com");
            manager.setPassword("manager123");
            manager.setRole(User.Role.MANAGER);
            manager.setStatus(User.UserStatus.ACTIVE);
            userService.createUser(manager);
            
            User developer = new User();
            developer.setName("John Doe");
            developer.setEmail("john.doe@maintenance.com");
            developer.setPassword("developer123");
            developer.setRole(User.Role.DEVELOPER);
            developer.setStatus(User.UserStatus.ACTIVE);
            userService.createUser(developer);
            
            User seniorDev = new User();
            seniorDev.setName("Alice Johnson");
            seniorDev.setEmail("alice.johnson@maintenance.com");
            seniorDev.setPassword("senior123");
            seniorDev.setRole(User.Role.SENIOR_DEVELOPER);
            seniorDev.setStatus(User.UserStatus.ACTIVE);
            userService.createUser(seniorDev);
            
            User developer2 = new User();
            developer2.setName("Bob Smith");
            developer2.setEmail("bob.smith@maintenance.com");
            developer2.setPassword("developer123");
            developer2.setRole(User.Role.DEVELOPER);
            developer2.setStatus(User.UserStatus.ACTIVE);
            userService.createUser(developer2);
            
            User developer3 = new User();
            developer3.setName("Carol Davis");
            developer3.setEmail("carol.davis@maintenance.com");
            developer3.setPassword("developer123");
            developer3.setRole(User.Role.SENIOR_DEVELOPER);
            developer3.setStatus(User.UserStatus.ACTIVE);
            userService.createUser(developer3);
        }
    }
    
    private void initializeSampleRequests() {
        if (requestService.getAllRequests().isEmpty()) {
            User developer = userService.getUserByEmail("john.doe@maintenance.com").orElse(null);
            User alice = userService.getUserByEmail("alice.johnson@maintenance.com").orElse(null);
            User bob = userService.getUserByEmail("bob.smith@maintenance.com").orElse(null);
            
            if (developer != null) {
                MaintenanceRequest sqlRequest = new MaintenanceRequest();
                sqlRequest.setTitle("Fix duplicate user records in accounts table");
                sqlRequest.setType(MaintenanceRequest.RequestType.SQL_FIX);
                sqlRequest.setStatus(MaintenanceRequest.RequestStatus.PENDING_APPROVAL);
                sqlRequest.setPriority(MaintenanceRequest.Priority.HIGH);
                sqlRequest.setApplication("Customer Management System");
                sqlRequest.setEnvironment(MaintenanceRequest.Environment.PRODUCTION);
                sqlRequest.setDescription("The accounts table has duplicate records for some users, causing authentication issues and incorrect billing calculations. This affects approximately 150 user accounts and needs immediate attention.");
                sqlRequest.setBusinessJustification("Users are unable to log in properly and billing discrepancies are occurring. This is impacting customer satisfaction and revenue accuracy.");
                sqlRequest.setRollbackPlan("Create backup of affected records before deletion. If issues occur, restore from backup using provided rollback script.");
                sqlRequest.setSqlStatement("DELETE FROM accounts WHERE id NOT IN (SELECT MAX(id) FROM accounts GROUP BY user_email, user_name);");
                sqlRequest.setPeerReviewer(alice);
                requestService.createRequest(sqlRequest, developer);
                
                MaintenanceRequest configRequest = new MaintenanceRequest();
                configRequest.setTitle("Update API Rate Limits");
                configRequest.setType(MaintenanceRequest.RequestType.CONFIG_UPDATE);
                configRequest.setStatus(MaintenanceRequest.RequestStatus.APPROVED);
                configRequest.setPriority(MaintenanceRequest.Priority.MEDIUM);
                configRequest.setApplication("Order Processing System");
                configRequest.setEnvironment(MaintenanceRequest.Environment.PRODUCTION);
                configRequest.setDescription("Increase API rate limits to handle increased traffic during peak hours.");
                configRequest.setBusinessJustification("Current rate limits are causing API timeouts during peak traffic periods.");
                configRequest.setRollbackPlan("Revert configuration file to previous version if performance issues occur.");
                configRequest.setConfigContent("api.rate.limit.requests.per.minute=1000\napi.rate.limit.burst.size=200");
                requestService.createRequest(configRequest, bob);
                
                MaintenanceRequest jobRequest = new MaintenanceRequest();
                jobRequest.setTitle("Restart Batch Process");
                jobRequest.setType(MaintenanceRequest.RequestType.JOB_CONTROL);
                jobRequest.setStatus(MaintenanceRequest.RequestStatus.COMPLETED);
                jobRequest.setPriority(MaintenanceRequest.Priority.HIGH);
                jobRequest.setApplication("Financial Reporting");
                jobRequest.setEnvironment(MaintenanceRequest.Environment.PRODUCTION);
                jobRequest.setDescription("Daily batch job failed and needs to be restarted to process pending transactions.");
                jobRequest.setBusinessJustification("Financial reports are delayed due to failed batch processing, affecting end-of-day reconciliation.");
                jobRequest.setRollbackPlan("Stop the job if it causes system performance issues.");
                jobRequest.setJobDetails("Job Name: DailyFinancialBatch\nSchedule: 02:00 AM daily\nAction: Restart");
                requestService.createRequest(jobRequest, alice);
            }
        }
    }
}
