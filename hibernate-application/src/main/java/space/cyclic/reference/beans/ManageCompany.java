package space.cyclic.reference.beans;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import space.cyclic.reference.interfaces.EagerBean;
import space.cyclic.reference.pojo.Company;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;

@EagerBean
@Singleton
public class ManageCompany {
    private static final Logger logger = Logger.getLogger(ManageCompany.class);

    private static SessionFactory sessionFactory;

    public ManageCompany() {
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
        Integer companyIDOne = this.addCompany("BEST COMPANY", "525 LOST IN PLAIN SIGHT", 9874563521L);
        Integer companyIDTwo = this.addCompany("MEGA CORPORATION", "654 NOT REAL STREET", 7845154112L);
        Integer companyIDThree = this.addCompany("SOCIAL CAPITALIST", "123 FAKE LANE", 6184028296L);

        this.listCompanies();

        this.updateCompanyPhoneNumber(companyIDOne, 31245163210L);

        this.deleteCompany(companyIDTwo);

        this.listCompanies();
    }

    public Integer addCompany(String companyName, String companyAddress, long phoneNumber) {
        Transaction tx = null;
        Integer companyID = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            Company company = new Company(companyName, companyAddress, phoneNumber);
            companyID = (Integer) session.save(company);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
        return companyID;
    }

    public void listCompanies() {
        Transaction tx = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            List companies = session.createQuery("FROM Company ").list();
            companies.stream().forEach(companyObject -> {
                Company company = (Company) companyObject;
                logger.info("Name: " + company.getName() +
                        " Address: " + company.getAddress() +
                        " Phone Number: " + company.getPhoneNumber());
            });
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    public void updateCompanyPhoneNumber(Integer CompanyID, long phoneNumber) {
        Transaction tx = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            Company company = session.get(Company.class, CompanyID);
            company.setPhoneNumber(phoneNumber);
            session.update(company);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    public void deleteCompany(Integer CompanyID) {
        Transaction tx = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            Company company = session.get(Company.class, CompanyID);
            session.delete(company);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error(e);
        } finally {
            if (Objects.nonNull(session))
                session.close();
        }
    }

    @Override
    public String toString() {
        return "Managed Company Bean.";
    }
}
