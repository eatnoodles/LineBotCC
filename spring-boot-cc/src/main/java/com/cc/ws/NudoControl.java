package com.cc.ws;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.cc.Application;

/**
 * @author Caleb Cheng
 *
 */
@Component
@Path("/API")
public class NudoControl {
	
	@Context
	HttpHeaders header;
	
	@Context
	HttpServletResponse response;
	
	@Path("/img/{image}")
	@Produces("image/png")
	public Response getImage(@PathParam("image") int num) {
		try {
			String path = String.format("img/%s.png", num > 100 ? "default" : num);
		    InputStream is = Application.class.getClassLoader().getResourceAsStream(path);
		    BufferedImage image = ImageIO.read(is);
		    if (image != null) {
		        OutputStream out = response.getOutputStream();
		        ImageIO.write(image, "png", out);
		        out.close();
		    }
		    return Response.noContent().build();
		} catch (Exception e) {
			return Response.noContent().build();
		}
	}
}
