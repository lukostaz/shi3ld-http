/**
 * Shi3ld - Shi3ld for HTTP is an access control module for enforcing 
 * authorization on triple stores. Shi3ld for HTTP protects HTTP operations 
 * on Linked Data and relies on attribute-based access policies.
 *
 * Copyright (C) 2013 Luca Costabello, Serena Villata,
 *  Oscar Rodriguez-Rocha, Fabien Gandon - v1.0
 * *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */



package fr.inria.shi3ld.exceptions;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;

/** Thrown to return a 400 Bad Request response with a list of error messages in the body. */
public class BadRequestException extends WebApplicationException
{
    private static final long serialVersionUID = 1L;
    private List<String> errors;

    public BadRequestException(String... errors)
    {
        this(Arrays.asList(errors));
    }

    public BadRequestException(List<String> errors)
    {
        super(Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_XHTML_XML)
                .entity(new GenericEntity<List<String>>(errors)
                {}).build());
        this.errors = errors;
    }

    public List<String> getErrors()
    {
        return errors;
    }
}
