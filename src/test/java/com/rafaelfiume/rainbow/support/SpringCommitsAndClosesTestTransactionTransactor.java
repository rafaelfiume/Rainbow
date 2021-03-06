package com.rafaelfiume.rainbow.support;

import com.rafaelfiume.salume.support.UnitOfWork;
import org.springframework.test.context.transaction.TestTransaction;

// Duplicated in Supplier
public class SpringCommitsAndClosesTestTransactionTransactor {

    public void perform(UnitOfWork unitOfWork) throws Exception {
        // Spring starts transaction...

        unitOfWork.work();  // Do the job

        // Now commit stuff
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

}
