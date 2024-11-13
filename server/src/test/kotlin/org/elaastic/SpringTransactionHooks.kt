package org.elaastic

import io.cucumber.java.After
import io.cucumber.java.Before
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition

/**
 *
 *
 * This class defines before and after hooks which provide automatic spring
 * rollback capabilities. These hooks will apply to any element(s) within a
 * `.feature` file tagged with `@txn`.
 *
 * From https://github.com/cucumber/cucumber-jvm/blob/main/examples/spring-java-junit5/src/test/java/io/cucumber/examples/spring/application/SpringTransactionHooks.java
 *
 * Clients wishing to leverage these hooks should include a copy of this class'
 * in their `glue` code.
 *
 *
 *
 * The BEFORE and AFTER hooks (both with hook order 100) rely on being able to
 * obtain a `PlatformTransactionManager` by type, or by an optionally
 * specified bean name, from the runtime `BeanFactory`.
 *
 *
 *
 * NOTE: This class is NOT threadsafe! It relies on the fact that cucumber-jvm
 * will instantiate an instance of any applicable hookdef class per scenario
 * run.
 *
 */
class SpringTransactionHooks : BeanFactoryAware {
    private var beanFactory: BeanFactory? = null
    private var transactionStatus: TransactionStatus? = null

    @Throws(BeansException::class)
    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    @Before(value = "@txn", order = 100)
    fun startTransaction() {
        transactionStatus = obtainPlatformTransactionManager()
            .getTransaction(DefaultTransactionDefinition())
    }

    fun obtainPlatformTransactionManager(): PlatformTransactionManager {
        return beanFactory!!.getBean(PlatformTransactionManager::class.java)
    }

    @After(value = "@txn", order = 100)
    fun rollBackTransaction() {
        obtainPlatformTransactionManager()
            .rollback(transactionStatus!!)
    }
}