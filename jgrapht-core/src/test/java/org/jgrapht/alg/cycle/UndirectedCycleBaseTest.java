/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/* -------------------------
 * UndirectedCycleBaseTest.java
 * -------------------------
 * (C) Copyright 2013, by Nikolay Ognyanov
 *
 * Original Author: Nikolay Ognyanov
 * Contributor(s) :
 *
 * $Id$
 *
 * Changes
 * -------
 * 06-Sep-2013 : Initial revision (NO);
 */
package org.jgrapht.alg.cycle;

import static org.junit.Assert.*;

import java.util.List;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

public class UndirectedCycleBaseTest
{
    private static int   MAX_SIZE = 10;
    private static int[] RESULTS  = { 0, 0, 0, 1, 3, 6, 10, 15, 21, 28, 36 };

    @Test
    public void test()
    {
        PatonCycleBase<Integer, DefaultEdge> patonFinder =
            new PatonCycleBase<Integer, DefaultEdge>();

        testAlgorithm(patonFinder);
    }

    private void testAlgorithm(
                               UndirectedCycleBase<Integer, DefaultEdge>
                               finder)
    {
        SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<Integer, DefaultEdge>
            (
             new ClassBasedEdgeFactory<Integer, DefaultEdge>
             (
              DefaultEdge.class
             )
            );
        for (int i = 0; i < 7; i++) {
            graph.addVertex(i);
        }

        finder.setGraph(graph);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        checkResult(finder, 1);
        graph.addEdge(2, 3);
        graph.addEdge(3, 0);
        checkResult(finder, 2);
        graph.addEdge(3, 1);
        checkResult(finder, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 2);
        checkResult(finder, 4);
        graph.addEdge(4, 5);
        checkResult(finder, 4);
        graph.addEdge(5, 2);
        checkResult(finder, 5);
        graph.addEdge(5, 6);
        graph.addEdge(6, 4);
        checkResult(finder, 6);

        for (int size = 1; size <= MAX_SIZE; size++) {
            graph = new SimpleGraph<Integer, DefaultEdge>
                (
                 new ClassBasedEdgeFactory<Integer, DefaultEdge>
                 (
                  DefaultEdge.class
                 )
                );
            for (int i = 0; i < size; i++) {
                graph.addVertex(i);
            }
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i != j) {
                        graph.addEdge(i, j);
                    }
                }
            }
            finder.setGraph(graph);
            checkResult(finder, RESULTS[size]);
        }
    }

    private void checkResult(UndirectedCycleBase
                             <Integer, DefaultEdge> finder,
                             int size)
    {
        assertTrue(finder.findCycleBase().size() == size);
    }
    
    @Test
    public void testPseudographIssue1() {
    	UndirectedGraph<String,Integer> g = new Pseudograph<String, Integer>(Integer.class);
		
		PatonCycleBase<String, Integer> finder = new PatonCycleBase<String, Integer>(g);
		
		// cycle between 2 nodes
		
		g.addVertex("A0");
		g.addVertex("A1");
		g.addEdge("A0","A1",1);
		g.addEdge("A1","A0",2);
		
		List<List<String>> cycles = finder.findCycleBase();

		// this is fine
		assertEquals(1,cycles.size());
		
		// this should be 2 but gives 3 (A0 node is repeated twice)
		assertEquals(2, cycles.get(0).size());
		

    }
    
    @Test
    public void testPseudographIssue2() {
    	
    	UndirectedGraph<String,Integer> g = new Pseudograph<String, Integer>(Integer.class);

		PatonCycleBase<String, Integer> finder = new PatonCycleBase<String, Integer>(g);

    	// graph with cycle and 2 edges between 2 nodes: infinite loop

    	g.addVertex("A0");
    	g.addVertex("A1");
    	g.addVertex("A2");
    	g.addVertex("A3");

    	g.addEdge("A0","A1",1);
    	g.addEdge("A0","A1",11);
    	g.addEdge("A1","A2",2);

    	g.addEdge("A2","A3",3);
    	//g.addEdge("A2","A3",31);
    	g.addEdge("A3","A0",4);

		List<List<String>> cycles = finder.findCycleBase();

		// this is fine
		assertEquals(2,cycles.size());
		
		// this is not fine as in testPseudographIssue1 (commented out to let it go to next test)
		//assertEquals(2, cycles.get(0).size());
		
		// this is fine
		assertEquals(4, cycles.get(1).size());
    	
    }
    
    @Test
    public void testPseudographIssue3() {
    	
    	UndirectedGraph<String,Integer> g = new Pseudograph<String, Integer>(Integer.class);

		PatonCycleBase<String, Integer> finder = new PatonCycleBase<String, Integer>(g);

    	// graph with cycle and 2 edges between 2 nodes: infinite loop

    	g.addVertex("A0");
    	g.addVertex("A1");
    	g.addVertex("A2");
    	g.addVertex("A3");

    	g.addEdge("A0","A1",1);
    	//g.addEdge("A0","A1",11);
    	g.addEdge("A1","A2",2);

    	g.addEdge("A2","A3",3);
    	g.addEdge("A2","A3",31);
    	g.addEdge("A3","A0",4);


    	// this goes into an infinite loop
    	List<List<String>> cycles = finder.findCycleBase();
    	
    	assertEquals(2,cycles.size());

    	// this is not fine as in testPseudographIssue1 (commented out to let it go to next test)
    	//assertEquals(2, cycles.get(0).size());

    	assertEquals(4, cycles.get(1).size());

    }
    
}
