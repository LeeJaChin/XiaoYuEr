package com.xiaoyuer;

import com.xiaoyuer.IdGenerator.SequenceGenerator;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
@Log4j2
public class App
{
    public static void main( String[] args )
    {

        SequenceGenerator sequenceGenerator = new SequenceGenerator();
        log.info("id : " + sequenceGenerator.nextId());
    }
}
