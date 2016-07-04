package space.cyclic.reference.beans;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import space.cyclic.reference.interfaces.EagerBean;
import space.cyclic.reference.pojo.Employee;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.List;

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
       /* Add few employee records in database */
        Integer empID1 = this.addEmployee("Zara", "Ali", 1000);
        Integer empID2 = this.addEmployee("Daisy", "Das", 5000);
        Integer empID3 = this.addEmployee("John", "Paul", 10000);
        Integer empID4 = this.addEmployee("Bunion", "Paul", 100000);

      /* List down all the employees */
        this.listEmployees();

      /* Update employee's records */
        this.updateEmployee(empID1, 5000);

      /* Delete an employee from the database */
        this.deleteEmployee(empID2);

      /* List down new list of the employees */
        this.listEmployees();

        this.listEmployeesSalaryOnly();

        this.listEmployeesSalaryOnly();

        this.listEmployeesByFirstName("Paul");
    }

    /* Method to CREATE an employee in the database */
    public Integer addEmployee(String fname, String lname, int salary) {
        Transaction tx = null;
        Integer employeeID = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Employee employee = new Employee(fname, lname, salary);
            employeeID = (Integer) session.save(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        }
        return employeeID;
    }

    /* Method to  READ all the employees */
    public void listEmployees() {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            List employees = session.createQuery("FROM Employee ").list();
            employees.stream().forEach(employeeObject -> {
                Employee employee = (Employee) employeeObject;
                logger.info("First Name: " + employee.getFirstName() +
                        "  Last Name: " + employee.getLastName() +
                        "  Salary: " + employee.getSalary());
            });
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        }
    }

    public void listEmployeesSalaryOnly() {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            List employees = session.createQuery("SELECT E.salary FROM Employee AS E").list();
            employees.stream().forEach(employeeObject -> {
                Integer salary = (Integer) employeeObject;
                logger.info("Salary: " + salary);
            });
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        }
    }

    public void listEmployeesByFirstName(String firstName) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            List employees = session.createQuery("SELECT E FROM Employee AS E WHERE E.firstName = :firstName")
                    .setParameter("firstName", firstName).list();
            employees.stream().forEach(employeeObject -> {
                Employee employee = (Employee) employeeObject;
                logger.info("First Name: " + employee.getFirstName() +
                        "  Last Name: " + employee.getLastName() +
                        "  Salary: " + employee.getSalary());
            });
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        }
    }

    /* Method to UPDATE salary for an employee */
    public void updateEmployee(Integer EmployeeID, int salary) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Employee employee = session.get(Employee.class, EmployeeID);
            employee.setSalary(salary);
            session.update(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        }
    }

    /* Method to DELETE an employee from the records */
    public void deleteEmployee(Integer EmployeeID) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Employee employee =
                    (Employee) session.get(Employee.class, EmployeeID);
            session.delete(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        }
    }

    @Override
    public String toString() {
        return "Managed Employee Bean.";
    }
}
