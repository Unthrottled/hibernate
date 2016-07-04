package space.cyclic.reference;

import org.apache.log4j.Logger;
import space.cyclic.reference.interfaces.EagerBean;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

public class ServiceExtension implements Extension {
    private static Logger logger = Logger.getLogger(ServiceExtension.class);
    private List<Bean<?>> eagerBeanList = new ArrayList<>();

    public <T> void collect(@Observes ProcessBean<T> event) {
        Bean<?> bean = event.getBean();
        if (event.getAnnotated().isAnnotationPresent(EagerBean.class)) {
            eagerBeanList.add(bean);
            logger.info("Eager Bean collect : " + bean.getBeanClass().getCanonicalName());
        }
    }

    public void load(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        for (Bean<?> bean : eagerBeanList) {
            logger.info("First call to : " + bean.getClass().getCanonicalName()
                    + "Eager Bean says : " + beanManager
                    .getReference(bean,
                            bean.getBeanClass(),
                            beanManager.createCreationalContext(bean)).toString());
        }
    }
}
