package edu.pitt.cs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.vm.Verify;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>
 * Uses the Java Path Finder model checking tool to check DrunkCarnivalShooter
 * shoot method for all scenarios. It enumerates all possible states of the
 * targets as well as all possible target choices by the user.
 */

public class DrunkCarnivalShooterTest extends TestJPF {
    private DrunkCarnivalShooter shooter; // The game object
    private StringBuilder builder; // The string builder object
    private boolean[] targets;
    private int targetChoice; // The user-inputted target choice to test (can be between 0 - 3)

    /**
     * Sets up the test fixture.
     */
    public void setUp() {
        targets = new boolean[4];
        
        // Generate choices for targetChoice using Verify.getInt(0, 3) API to cover all possible target inputs (0-3).
        targetChoice = Verify.getInt(0, 3);
        
        // Enumerate each of the four target states as true (standing) or false (knocked down) using Verify.getBoolean().
        for (int i = 0; i < targets.length; i++) {
            targets[i] = Verify.getBoolean();
        }

        // Create the game instance
        shooter = DrunkCarnivalShooter.createInstance(InstanceType.IMPL);
        
        // Set up the targets in the game to reflect the generated targets array
        for (int i = 0; i < targets.length; i++) {
            if (!targets[i]) {
                shooter.takeDownTarget(i);
            }
        }

        // Initialize the string builder
        builder = new StringBuilder();
    }

    /**
     * Test case for boolean shoot(int t, StringBuilder builder).
     * 
     * <pre>
     * Preconditions: targetChoice has been initialized with a target number.
     *                shooter is instantiated with DrunkCarnivalShooter.createInstance() with preconfigured targets.
     *                builder is instantiated with new StringBuilder().
     * Execution steps: Call shooter.shoot(targetChoice, builder);
     * Invariant: The number of targets which returns true on shooter.isTargetStanding(i)
     *            where i = 0 ... 3 is equal to shooter.getRemainingTargetNum().
     * </pre>
     */
    @Test
    public void testShoot() {
        // Run this test in JPF VM only
        if (verifyNoPropertyViolation() == false) {
            return;
        }
        
        // Set up the game state with all possible configurations
        setUp();

        // Generate a descriptive failString for debugging and verification
        String failString = "Failure in " + shooter.getRoundString() + " (targetChoice=" + targetChoice + "):";
        
        // Execute the shoot method with the current targetChoice and capture the result
        shooter.shoot(targetChoice, builder);
        
        // Verify that the remaining target count matches the number of standing targets
        int standingCount = 0;
        for (int i = 0; i < targets.length; i++) {
            if (shooter.isTargetStanding(i)) {
                standingCount++;
            }
        }
        
        // Assert that the remaining target count is accurate
        assertEquals(failString, standingCount, shooter.getRemainingTargetNum());
    }
}
