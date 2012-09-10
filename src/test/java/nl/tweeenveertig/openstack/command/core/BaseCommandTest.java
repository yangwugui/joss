package nl.tweeenveertig.openstack.command.core;

import nl.tweeenveertig.openstack.command.identity.access.Access;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseCommandTest {

    @Mock
    protected Access defaultAccess;

    @Mock
    protected HttpClient httpClient;

    @Mock
    protected HttpResponse response;

    @Mock
    protected HttpEntity httpEntity;

    @Mock
    protected StatusLine statusLine;

    public void setup() throws IOException {
        InputStream inputStream = IOUtils.toInputStream("");
        when(defaultAccess.getInternalURL()).thenReturn("http://someurl.nowhere");
        when(httpEntity.getContent()).thenReturn(inputStream);
        when(response.getEntity()).thenReturn(httpEntity);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpRequestBase.class))).thenReturn(response);
    }

    protected void checkForError(int httpStatusCode, AbstractCommand command, CommandExceptionError expectedError) throws IOException {
        when(statusLine.getStatusCode()).thenReturn(httpStatusCode);
        try {
            command.execute();
            fail("Should have thrown an exception");
        } catch (CommandException err) {
            assertEquals(expectedError, err.getError());
        }
    }

    protected void prepareHeader(HttpResponse response, String name, String value, List<Header> headers) {
        Header header = Mockito.mock(Header.class);
        when(header.getName()).thenReturn(name);
        when(header.getValue()).thenReturn(value);
        when(response.getHeaders(name)).thenReturn(new Header[] { header } );
        if (headers != null) {
            headers.add(header);
        }
    }

    protected void prepareHeader(HttpResponse response, String name, String value) {
        prepareHeader(response, name, value, null);
    }

}