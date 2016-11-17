package bus;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BusStop {

    private boolean pmrs = false;
    private String busLine, destination, timeLeft, id;

    public BusStop(String id){
        this.id = id;
    }


    /**
     * 
     * @param busLine A bus line. Might be something like "30" or "N2"
     * @param destination The bus destination
     * @param timeLeft Time left for the bus to come
     * @param pmrs true if the bus is ready for PRM people
     * @param id a bus stop number
     */
    public BusStop(String busLine, String destination, String timeLeft, boolean pmrs, int id){
        this.busLine = busLine;
        this.destination = destination;
        this.timeLeft = timeLeft;
        this.pmrs = pmrs;
    }

    /**
     * A single data row should look like this:
     *
     * <tr>
     *  <td class="digital">30</td>
     *  <td class="digital">LAS FUENTES</td>
     *  <td class="digital">3 minutos.</td>
     *  <td class="digital"></td>
     *  <td class="digital"></td>
     * </tr>
     *
     * @param row - A single row full of data
     * @param id - A bus stop number
     */
    public BusStop(Element row, String id){
        Elements columns = row.children();
        this.busLine = columns.get(0).text();
        this.destination = columns.get(1).text();
        this.timeLeft = columns.get(2).text();
        this.pmrs = columns.get(3).hasText();
        this.id = id; 
    }


	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(); 
		sb.append(" - Línea ").append(busLine).append(" a ").append(destination).append(": ").append(timeLeft); 
		return sb.toString();
	}
    
    






}
