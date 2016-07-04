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
        try{
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }catch (Throwable ex) {
            logger.fatal("Failed to create sessionFactory object.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Web application is set for this eager bean to be created after deployment.
     * Method below will be executed after instantiation of this singleton object.
     *
     * Will interact with the mysql database 'hibernate' and add, remove, and
     * update n-tuple in the table.
     *
     * My way of having a main method in a web application. Could be replaced by
     * another way, but this is what I know.
     */
    @PostConstruct
    public void init() {
       /* Add few employee records in database */
        Integer empID1 = this.addEmployee("Zara", "Ali", 1000);
        Integer empID2 = this.addEmployee("Daisy", "Das", 5000);
        Integer empID3 = this.addEmployee("John", "Paul", 10000);

      /* List down all the employees */
        this.listEmployees();

      /* Update employee's records */
        this.updateEmployee(empID1, 5000);

      /* Delete an employee from the database */
        this.deleteEmployee(empID2);

      /* List down new list of the employees */
        this.listEmployees();
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
            for (Object employee1 : employees) {
                Employee employee = (Employee) employee1;
                System.out.print("First Name: " + employee.getFirstName());
                System.out.print("  Last Name: " + employee.getLastName());
                System.out.println("  Salary: " + employee.getSalary());
            }
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