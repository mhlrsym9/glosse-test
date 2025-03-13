package com.transparent.glossary;

import com.transparent.glossary.io.GlossaryInputDataReader;
import com.transparent.glossary.model.SortObject;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Automated testing of the GlossaryInputDataReader object.
 */
@ContextConfiguration( locations = "/glottal-config.xml" )
public class GlossaryInputDataReaderIntegrationTest extends AbstractJUnit4SpringContextTests
{
    @Resource( name = "b4xReader" )
    private GlossaryInputDataReader sut;

    @Test
    public void givenValidData_readSortObjects_returns_populated_collection() throws Exception
    {
        final File file = new File( "./testdata/sample.xml" );
        sut.setInputFile( file );
        final Collection<SortObject> sortObjects = sut.readSortObjects();
        assertThat( sortObjects, notNullValue()  );
        assertThat( sortObjects.size(), is( equalTo( 2 ) )  );
        final SortObject[] data = sortObjects.toArray( new SortObject[sortObjects.size()] );
        for( int i = 0; i < data.length; i++ )
        {
            final SortObject sortObject = data[i];
            assertThat( sortObject.getReferencedBy(), is( equalTo( "Unit 00" ) ) );
            if ( 0 == i )
            {
                assertThat( sortObject.getSideOneSoundFile(), is( equalTo( "q_DphYggqmU9AO_zr1C" ) ) );
                assertThat( sortObject.getSideTwoSoundFile(), is( equalTo( "JngBr_ZY5Iu1TsI" ) ) );
            }
            else
            {
                assertThat( sortObject.getSideOneSoundFile(), is( equalTo( "fnOjLrJWoDPYxI9nVJzV4jtaKUk_" ) ) );
                assertThat( sortObject.getSideTwoSoundFile(), is( equalTo( "AKGR3V98CzGABSy" ) ) );
            }
        }
    }

}
