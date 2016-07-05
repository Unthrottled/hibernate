package space.cyclic.reference.beans;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import space.cyclic.reference.interfaces.EagerBean;
import space.cyclic.reference.pojo.Employee;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;

@EagerBean
@Singleton
public class ManageEmployee {
    private static final Logger logger = Logger.getLogger(ManageEmployee.class);
    private static SessionFactory sessionFactory;

    public ManageEmployee() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    /**
     * Web application is set for this eager bean to be created after deployment.
     * Method below will be executed after instantiation of this singleton object.
     * <p/>
     * Will interact with the mysql database 'hibernate' and add, remove, and
     * update n-tuple in the table.
     * <p/>
     * My way of having a main method in a web application. Could be replaced by
     * another way, but this is what I know.
     * <p/>
     * All standard output, ie 'System.out.println()' or 'System.err.println()' is
     * redirected to output file hibernate.log, to path configured by system property:
     * 'space.cyclic.reference.log.path'
     */
    @PostConstruct
    public void init() {
        Integer empID1 = this.addEmployee("Zara", "Ali", 1000);
        Integer empID2 = this.addEmployee("Daisy", "Das", 5000);
        Integer empID3 = this.addEmployee("John", "Paul", 10000);
        Integer empID4 = this.addEmployee("Bunion", "Paul", 100000);
        Integer empID5 = this.addEmployee("Mohd", "Yasee", 3000);

        this.listEmployees();

        this.updateEmployee(empID1, 5000);

        this.deleteEmployee(empID2);

        this.listEmployees();

        this.listEmployeesSalaryOnly();

        this.listEmployeesSalaryOnly();

        this.listEmployeesByFirstName("Paul");

        this.printNumberOfEmployees();

        this.printTotalSalaryForAllEmployees();
    }

    public Integer addEmployee(String fname, String lname, int salary) {
        Transaction transaction = null;
        Integer employeeID = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Employee employee = new Employee(fname, lname, salary);
            employeeID = (Integer) session.save(employee);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
        return employeeID;
    }

    public void listEmployees() {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            List employees = session.createQuery("FROM Employee ").list();
            employees.stream().forEach(employeeObject -> {
                Employee employee = (Employee) employeeObject;
                logger.info("First Name: " + employee.getFirstName() +
                        "  Last Name: " + employee.getLastName() +
                        "  Salary: " + employee.getSalary());
            });
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    public void listEmployeesSalaryOnly() {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            List employees = session.createQuery("SELECT E.salary FROM Employee AS E").list();
            employees.stream().forEach(employeeObject -> {
                Integer salary = (Integer) employeeObject;
                logger.info("Salary: " + salary);
            });
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    public void listEmployeesByFirstName(String firstName) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            List employees = session.createQuery("SELECT E FROM Employee AS E WHERE E.firstName = :firstName")
                    .setParameter("firstName", firstName).list();
            employees.stream().forEach(employeeObject -> {
                Employee employee = (Employee) employeeObject;
                logger.info("First Name: " + employee.getFirstName() +
                        "  Last Name: " + employee.getLastName() +
                        "  Salary: " + employee.getSalary());
            });
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    public void updateEmployee(Integer EmployeeID, int salary) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Employee employee = session.get(Employee.class, EmployeeID);
            employee.setSalary(salary);
            session.update(employee);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    public void deleteEmployee(Integer EmployeeID) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Employee employee =
                    (Employee) session.get(Employee.class, EmployeeID);
            session.delete(employee);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    public void printNumberOfEmployees() {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Criteria cr = session.createCriteria(Employee.class);

            // To get total row count.
            cr.setProjection(Projections.rowCount());
            List rowCount = cr.list();

            logger.info("Total Employee Count: " + rowCount.get(0));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            logger.warn(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    public void printTotalSalaryForAllEmployees() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Criteria sessionCriteria = session.createCriteria(Employee.class);

            sessionCriteria.setProjection(Projections.sum("salary"));
            List totalSalary = sessionCriteria.list();

            logger.info("Total Salary: " + totalSalary.get(0));
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            logger.warn(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    @Override
    public String toString() {
        return "Managed Employee Bean.";
    }
}
