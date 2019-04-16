package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure generate(String employeeId) {
        Employee employee = employeeService.read(employeeId);
        return generate(employee);
    }

    @Override
    public ReportingStructure generate(Employee employee) {
        LOG.debug("Creating reportingStructure for employee [{}]", employee);

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);

        int numberOfReports = calculateNumberOfReports(employee);
        reportingStructure.setNumberOfReports(numberOfReports);

        return reportingStructure;
    }

    /**
     * Calculates the number of Employees which report to the provided Employee.
     * The number of reports includes all direct reports as well as
     * reports (direct or indirect) of those direct reports.
     *
     * @param employee The Employee for which to calculate the number of reports
     * @return The number of reports or 0 if the Employee has no reports
     */
    private int calculateNumberOfReports(Employee employee) {
        employee = ensureEmployeeData(employee);

        int numberOfReports = 0;
        if (employee.getDirectReports() != null) {
            for (Employee report : employee.getDirectReports()) {
                numberOfReports++;
                numberOfReports += calculateNumberOfReports(report);
            }
        }
        return numberOfReports;
    }

    /**
     * Uses the EmployeeService to ensure that Employee information is present.
     *
     * @param employee The Employee which may or may not include data besides a valid ID
     * @return The specified Employee including all associated data
     */
    private Employee ensureEmployeeData(Employee employee) {
        return employee.getLastName() != null
                ? employee
                : employeeService.read(employee.getEmployeeId());
    }
}
