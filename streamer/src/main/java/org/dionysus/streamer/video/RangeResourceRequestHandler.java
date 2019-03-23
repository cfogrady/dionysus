package org.dionysus.streamer.video;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

@Named
public class RangeResourceRequestHandler extends ResourceHttpRequestHandler {

    public static final String FILE_ATTRIBUTE = "RANGE_RESOURCE_REQUEST_HANDLER_FILE_ATTRIBUTE";

    @Inject
    public RangeResourceRequestHandler() {}

    @Override
    protected Resource getResource(HttpServletRequest request) {

        final File file = (File) request.getAttribute(FILE_ATTRIBUTE);
        return new FileSystemResource(file);
    }
}
