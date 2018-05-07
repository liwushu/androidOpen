package emoji.flying.com.testinstrument;

import android.util.Log;

import org.junit.Test;

import emoji.flying.com.testinstrument.utils.StringUtils;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testStringUtils() {
        boolean isEmpty = StringUtils.isEmpty("aaaaa");
        boolean isEmpty2 = StringUtils.isEmpty("");
        //System.out.println("isEmpty: "+isEmpty+"   isEmpty2: "+isEmpty2  );
        Log.e("TAG","isEmpty: "+isEmpty+"   isEmpty2: "+isEmpty2  );
    }
}