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


package fr.inria.shi3ld.services;

import fr.inria.acacia.corese.api.EngineFactory;
import fr.inria.acacia.corese.api.IEngine;

public class Store {
	public static EngineFactory ef;
	public static IEngine engine;
}
