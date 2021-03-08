import static org.junit.Assert.*;

import org.junit.Test;

public class test {

	@Test
	public void testDurationCalculation() {
		assertEquals("00hrs failure", "00hrs 13mins" ,Timetable.calculateDuration("0753", "0806"));
		assertEquals("00mins failure", "05hrs 00mins" ,Timetable.calculateDuration("0600", "1100"));
		assertEquals("general failure", "01hrs 30mins" ,Timetable.calculateDuration("1230", "1400"));
		assertEquals("00hrs 00mins failure", "00hrs 00mins" ,Timetable.calculateDuration("1500", "1500"));
	}
}
