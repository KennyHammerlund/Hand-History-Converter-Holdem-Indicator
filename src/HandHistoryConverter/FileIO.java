package HandHistoryConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FileIO {

	public static Connection connectJDBC(String path) {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + path);
			System.out.println("Opened database successfully");
			return c;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return c;
	}

	/**
	 * Writes list of hands to file specified by user
	 * 
	 * @param file
	 * @param handList
	 *            list of hands <String> created from list on page
	 * @return
	 */
	public static boolean writeFile(File file, List<String> handList) {
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			for (String s : handList) {
				writer.println(s);
				writer.println("");
				writer.println("");
				writer.println("");
			}
			return true;
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding");
			e.printStackTrace();
		}
		return false;
	}

	public static String displayDB(Connection c) {
		Statement stmt = null;
		try {
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM hands_1;");

			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("stakes");

				System.out.print("ID = " + id);
				System.out.print(" | STAKES = " + name);

				System.out.println();
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		System.out.println("Operation done successfully");

		return "";
	}

	/**
	 * Creates Hands based on the XML database column
	 * 
	 * @param c
	 *            JDBC Connector
	 * @return Array of hands in ArrayList<hands>
	 */
	public static List<Hand> createhands(Connection c) {
		Statement stmt = null;
		List<Hand> retList = new ArrayList<Hand>();
		try {
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM hands_1;");

			while (rs.next()) {
				int id = rs.getInt("id");
				String xmlString = rs.getString("xml_dump");
				String website = rs.getString("site");
				retList.add(processXML(xmlString, website));
			}

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		System.out.println("Hands Created successfully");

		return retList;
	}

	private static Hand processXML(String XML, String website) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;
		Hand retHand = null;
		try {
			builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(XML));

			doc = builder.parse(is);

			// Create XPathFactory object
			XPathFactory xpathFactory = XPathFactory.newInstance();

			// Create XPath object
			XPath xpath = xpathFactory.newXPath();
			// <G id="3434602178" dt="1489095440" stake="4/8" limit="1" game="1"
			// type="1" cur="$" seats="10" ver="1" pot="148" rake="3"
			// pc="7c,6s,Jh,5c,9d">
			String handID = getHandID(doc, xpath);
			String stakes = getStakesCash(doc, xpath);
			String time = getTime(doc, xpath);
			String limit = getLimit(doc, xpath);
			double pot = getPot(doc, xpath);
			int buttonPosition = getButtonPos(doc, xpath);
			website = siteConvert(website);
			String action = getAction(doc, xpath, website);

			retHand = new Hand(time, handID, website, stakes, limit, pot, buttonPosition, action);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return retHand;
	}

	/**
	 * Takes numeric value from HEI and returns the name of the site associated
	 * 
	 * @param website
	 *            numeric value from hei
	 * @return name
	 */
	private static String siteConvert(String website) {
		String site = "";
		switch (Integer.valueOf(website)) {
		case (7):
			site = "Ignition";
			break;
		case (27):
			site = "Full Tilt";
			break;
		case (26):
			site = "Sky Poker";
			break;
		default:
			site = "Unknown";
			break;
		}

		return site;
	}

	private static String getAction(Document doc, XPath xpath, String website) {
		String actFull = "";
		String[] table = new String[11];
		String[] cards = new String[11];
		Double amount = null;
		String actionValue = "";
		try {
			XPathExpression curExpr = xpath.compile("G/@cur");
			String currency = (String) curExpr.evaluate(doc, XPathConstants.STRING);

			XPathExpression tableExpr = xpath.compile("G/PS/P");
			NodeList tableNodes = (NodeList) tableExpr.evaluate(doc, XPathConstants.NODESET);
			// Position of Hero
			XPathExpression heroExpr = xpath.compile("G/PS/@self");
			Integer hero = Integer.valueOf((String) heroExpr.evaluate(doc, XPathConstants.STRING));

			// board
			XPathExpression boardExpr = xpath.compile("G/@pc");
			String board = (String) boardExpr.evaluate(doc, XPathConstants.STRING);
			String[] splitBoard = board.split(",");
			int c = 1;
			String flop = "";
			String turn = "";
			String river = "";
			String endPlayers = "";
			for (String s : splitBoard) {
				if (c <= 3) {
					flop += s + " ";
				} else if (c == 4) {
					turn = s;
				} else {
					river = s;
				}
				c++;
			}
			flop = flop.trim();

			// Pot and Rake
			XPathExpression potExpr = xpath.compile("G/@pot");
			Double potSize = Double.valueOf((String) potExpr.evaluate(doc, XPathConstants.STRING));
			XPathExpression rakeExpr = xpath.compile("G/@rake");
			Double rakeAmt = Double.valueOf((String) rakeExpr.evaluate(doc, XPathConstants.STRING));
			XPathExpression idExpr = xpath.compile("G/@id");
			long idAmt = Long.parseLong(((String) idExpr.evaluate(doc, XPathConstants.STRING)));

			// set up player positions
			int position = 0;
			for (int j = 0; j < tableNodes.getLength(); j++) {
				position = Integer.valueOf(tableNodes.item(j).getAttributes().getNamedItem("s").getNodeValue());
				String pName = tableNodes.item(j).getAttributes().getNamedItem("name").getNodeValue();
				String pChips = tableNodes.item(j).getAttributes().getNamedItem("chips").getNodeValue();

				// Set Up Players Cards
				String pCards = "";
				if (tableNodes.item(j).getAttributes().getNamedItem("c") != null) {
					pCards = tableNodes.item(j).getAttributes().getNamedItem("c").getNodeValue();
					if (pCards.equals(",")) {
						pCards = "";
					} else {
						pCards = pCards.replace(",", " ");
					}
				}

				cards[position] = pCards;
				if (position == hero) {
					table[position] = "Hero";
				} else {
					table[position] = pName;
				}
				actFull += "Seat " + position + ": " + table[position] + " (" + currency + pChips + " in chips) \n";
			}

			XPathExpression expr = xpath.compile("G/AS/A");
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

			int stop = 1;
			// Action Loop
			for (int i = 0; i < 2; i++) {
				stop++;
				String player = "";
				if (nodes.item(i).getAttributes().getNamedItem("s") != null) {
					player = table[Integer.valueOf(nodes.item(i).getAttributes().getNamedItem("s").getNodeValue())];
				}

				int actionType = Integer.valueOf(nodes.item(i).getAttributes().getNamedItem("type").getNodeValue());
				// if tag has a v associated with it set it to amount otherwise
				// set amount to null
				if (nodes.item(i).getAttributes().getNamedItem("v") != null) {
					amount = Double.valueOf(nodes.item(i).getAttributes().getNamedItem("v").getNodeValue());
					String amountString = amount.toString();
					// check for whole number if it is remove decimal
					String[] splitNumber = amount.toString().split("\\.");
					if (splitNumber[1].equals("0") | splitNumber[1].equals("00")) {
						amountString = splitNumber[0];
					}
					actionValue = currency + amountString;
				} else {
					amount = null;
				}
				// Break out if action is hole cards
				if (actionType == 27) {
					break;
				}

				if (amount == null) {
					actFull += getActionString(actionType, player) + "\n";
				} else {
					actFull += getActionString(actionType, player, actionValue) + "\n";
				}
			}

			actFull += "*** HOLE CARDS ***\n";
			actFull += "Dealt to Hero [" + cards[hero] + "]\n";

			// Loop after Cards are dealt
			for (int i = stop; i < nodes.getLength(); i++) {
				String player = "";
				if (nodes.item(i).getAttributes().getNamedItem("s") != null) {
					player = table[Integer.valueOf(nodes.item(i).getAttributes().getNamedItem("s").getNodeValue())];
				}

				int actionType = Integer.valueOf(nodes.item(i).getAttributes().getNamedItem("type").getNodeValue());

				/*
				 * if tag has a v associated with it set it to amount otherwise
				 * set amount to null DETERMINES THE OVERLOADED FUNCTION
				 */
				if (nodes.item(i).getAttributes().getNamedItem("v") != null) {
					amount = Double.valueOf(nodes.item(i).getAttributes().getNamedItem("v").getNodeValue());
					String amountString = amount.toString();
					// check for whole number if it is remove decimal
					String[] splitNumber = amount.toString().split("\\.");
					if (splitNumber[1].equals("0") | splitNumber[1].equals("00")) {
						amountString = splitNumber[0];
					}
					actionValue = currency + amountString;
				} else {
					amount = null;
				}

				/*
				 * Sets The Lines of the action
				 */
				if (amount == null) {
					actFull += getActionString(actionType, player);
					switch (actionType) {
					case 28:
						actFull += " [" + flop + "]";
						break;
					case 29:
						actFull += " [" + flop + "]" + " [" + turn + "]";
						break;
					case 30:
						actFull += " [" + flop + "]" + " [" + turn + "]" + " [" + river + "]";
						break;
					case 26:
						Integer winSeat = Integer
								.valueOf(nodes.item(i).getAttributes().getNamedItem("s").getNodeValue());
						actFull += table[winSeat] + ": shows [" + cards[winSeat] + "]";
						break;
					}
					actFull += "\n";

				} else {
					// SHOWDOWN
					if (actionType == 14 && nodes.item(i).getAttributes().getNamedItem("s") != null) {
						String winner = nodes.item(i).getAttributes().getNamedItem("s").getNodeValue();
						Integer winNum = Integer.valueOf(winner);
						if (winNum > 10) {
							System.out.println("Winner: " + winner);
						}

						if (nodes.item(i).getAttributes().getNamedItem("v") != null) {
							Double collected = Double
									.valueOf(nodes.item(i).getAttributes().getNamedItem("v").getNodeValue());
							actFull += table[winNum] + " collected " + currency + collected + " from the pot\n";
						}
					} else {
						actFull += getActionString(actionType, player, actionValue);
					}
					if (!(nodes.getLength() - 1 == i)) {
						actFull += "\n";
					}
				}

			}

			// org.w3c.dom.Node summaryNode =
			// nodes.item(nodes.getLength()).getAttributes().getNamedItem("s");
			// summary
			actFull += "*** SUMMARY ***\n";
			actFull += "Total Pot " + currency + potSize + " | Rake " + currency + rakeAmt + "\n";
			if (!flop.equals("")) {
				actFull += "Board [" + flop + " " + turn + " " + river + "]\n";
			}

			XPathExpression btnExpr = xpath.compile("G/PS/@dealer");
			String btnPos = (String) btnExpr.evaluate(doc, XPathConstants.STRING);

			// Build Header
			String header = website + " Hand #" + idAmt + ": " + getGameName(doc, xpath) + " "
					+ getStakesCash(doc, xpath) + " - " + getDate(doc, xpath) + "\n" + "Table '"
					+ getTableName(doc, xpath) + "' " + getMaxPlayers(doc, xpath) + " Seat #" + btnPos
					+ " is the button\n";

			actFull = header + actFull;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return actFull;
	}

	private static String getMaxPlayers(Document doc, XPath xpath) {
		String retVal = "";
		try {
			XPathExpression typeExpr = xpath.compile("G/@seats");
			retVal = (String) typeExpr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		retVal += "-max";
		return retVal;
	}

	private static String getTableName(Document doc, XPath xpath) {
		String retVal = "";

		try {
			XPathExpression typeExpr = xpath.compile("G/title");
			retVal = (String) typeExpr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		retVal = retVal.replaceFirst(".+? -", "");
		retVal = retVal.trim();
		return retVal;
	}

	/**
	 * Returns String of time hand Was played
	 * 
	 * @param doc
	 * @param xpath
	 * @return
	 */
	private static String getDate(Document doc, XPath xpath) {
		String retVal = null;
		long longInt = 0;
		try {
			XPathExpression typeExpr = xpath.compile("G/@dt");
			longInt = Long.parseLong((String) typeExpr.evaluate(doc, XPathConstants.STRING));
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		ZonedDateTime zDate = Instant.ofEpochSecond(longInt).atZone(ZoneId.of("GMT-5"));

		retVal = zDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")) + " CST";

		return retVal;
	}

	private static String getGameName(Document doc, XPath xpath) {
		String retVal = null;
		Integer type = null;
		try {
			XPathExpression typeExpr = xpath.compile("G/@type");
			type = Integer.valueOf((String) typeExpr.evaluate(doc, XPathConstants.STRING));
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		switch (type) {
		case (1):
			retVal = getCashGameName(doc, xpath);
			break;
		case (3):
			retVal = getSNGGameName(doc, xpath);
			break;
		}
		return retVal;
	}

	/**
	 * Returns string representation of the name of Sit N go
	 * 
	 * @param doc
	 * @param xpath
	 * @return
	 */
	private static String getSNGGameName(Document doc, XPath xpath) {
		String retVal = "";

		return retVal;
	}

	/**
	 * Creates string representation of the name of the game
	 * 
	 * @param doc
	 * @param xpath
	 * @return
	 */
	private static String getCashGameName(Document doc, XPath xpath) {
		String retVal = "";
		Integer game = null, type = null, limit = null;
		// get values
		try {
			XPathExpression gameExpr = xpath.compile("G/@game");
			game = Integer.valueOf((String) gameExpr.evaluate(doc, XPathConstants.STRING));

			XPathExpression limitExpr = xpath.compile("G/@limit");
			limit = Integer.valueOf((String) limitExpr.evaluate(doc, XPathConstants.STRING));
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		switch (game) {
		case (1):
			retVal += "Hold'em";
			break;
		case (2):
			retVal += "Omaha";
			break;
		case (3):
			retVal += "";
			break;
		}

		switch (limit) {
		case (1):
			retVal += " Limit";
			break;
		case (2):
			retVal += " Pot Limit";
			break;
		case (3):
			retVal += " No Limit";
			break;
		}
		return retVal;

	}

	/**
	 * Returns string representation when there is an action with a value
	 * associated
	 * 
	 * @param actionType
	 *            int value from holdem Indicator
	 * @param player
	 *            String of player name
	 * @param amount
	 *            value of action with currency symbol
	 * @return line to add to output
	 */
	private static String getActionString(int actionType, String player, String amount) {
		String action = "";
		switch (actionType) {
		case 1:
			action = player + ": posts small blind " + amount;
			break;
		case 2:
			action = player + ": posts big blind " + amount;
			break;
		case 4:
			action = player + ": calls " + amount;
			break;
		case 7:
			action = player + ": bets " + amount;
			break;
		case 10:
			action = player + ": raises " + amount;
			break;
		case 19:
			action = player + ": posts the ante " + amount;
			break;
		case 17:
			action = amount + " returned to " + player;
			break;
		case 26:
			action = "*** SHOW DOWN ***";
			break;
		}
		return action;
	}

	/**
	 * Returns string representation when there is an action with no value
	 * associated
	 * 
	 * @param actionType
	 *            int value from Holdem Indicator XML file
	 * @param player
	 *            String of player name
	 * @return Line to add to the final string output
	 */
	private static String getActionString(int actionType, String player) {
		String action = "";
		switch (actionType) {
		case 3:
			action = player + ": folds";
			break;
		case 6:
			action = player + ": checks";
			break;
		case 27:
			action = "*** HOLE CARDS ***";
			break;
		case 28:
			action = "*** FLOP ***";
			break;
		case 29:
			action = "*** TURN ***";
			break;
		case 30:
			action = "*** RIVER ***";
			break;
		}
		return action;
	}

	/**
	 * Process XML document from Holdem Indicator and return button position
	 * 
	 * @param doc
	 *            XML document
	 * @param xpath
	 *            xpath Object
	 * @return int button position
	 */
	private static int getButtonPos(Document doc, XPath xpath) {
		int retVal = 0;
		try {
			XPathExpression expr = xpath.compile("G/PS/@dealer");

			String value = (String) expr.evaluate(doc, XPathConstants.STRING);
			retVal = Integer.valueOf(value);

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * Process XML document from Holdem Indicator and returns value of the pot
	 * 
	 * @param doc
	 *            XML Document
	 * @param xpath
	 *            xpath Object
	 * @return double value of the pot
	 */
	private static double getPot(Document doc, XPath xpath) {
		double retVal = 0;
		try {
			XPathExpression expr = xpath.compile("G/@pot");

			String value = (String) expr.evaluate(doc, XPathConstants.STRING);
			retVal = Double.parseDouble(value);

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * Returns Limit type in String form 1: Limit 2: pot Limit 3: No Limit
	 * 
	 * @param doc
	 * @param xpath
	 * @return
	 */
	private static String getLimit(Document doc, XPath xpath) {
		String retVal = null;
		try {
			XPathExpression expr = xpath.compile("G/@limit");

			retVal = (String) expr.evaluate(doc, XPathConstants.STRING);

			switch (Integer.valueOf(retVal)) {
			case 1:
				retVal = "Limit";
				break;
			case 2:
				retVal = "Pot Limit";
				break;
			case 3:
				retVal = "No Limit";
				break;
			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * Returns a string representing the time
	 * 
	 * @param doc
	 *            XML Document
	 * @param xpath
	 *            Xpath Object
	 * @return
	 */
	private static String getTime(Document doc, XPath xpath) {
		String retVal = null;
		try {
			XPathExpression expr = xpath.compile("G/@dt");

			String value = (String) expr.evaluate(doc, XPathConstants.STRING);
			Integer timeInt = Integer.valueOf(value);
			retVal = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(timeInt * 1000L));
			retVal += " EET [" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(timeInt * 1000L)) + " ET]";
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * Gets the value of the stakes for a cash game and returns the pokerstars
	 * format
	 * 
	 * @param doc
	 *            XML document
	 * @param xpath
	 *            xpath object
	 * @return String pokerstars stakes
	 */
	private static String getStakesCash(Document doc, XPath xpath) {
		String retVal = null;
		String curVal = null;
		String symbol = null;
		try {
			XPathExpression expr = xpath.compile("G/@stake");
			retVal = (String) expr.evaluate(doc, XPathConstants.STRING);

			// Grab Currency marker and change to currency
			XPathExpression currency = xpath.compile("G/@cur");
			symbol = (String) currency.evaluate(doc, XPathConstants.STRING);
			if (symbol.equals("$")) {
				curVal = "USD";
			} else if (symbol.equals("\u20ac")) {
				curVal = "EUR";
			} else if (symbol.equals("\u00a3")) {
				curVal = "GBP";
			}

			// Convert to format for pokerstars
			Pattern regex = Pattern.compile("(\\d*\\.?\\d*?)/(\\d*\\.?\\d*)");
			Matcher matcher = regex.matcher(retVal);
			if (matcher.find()) {
				String sb = matcher.group(1);
				String bb = matcher.group(2);
				retVal = "(" + symbol + sb + "/" + symbol + bb + " " + curVal + ")";
			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * Returns the ID number of a Hand from Holdem Indicator XML
	 * 
	 * @param doc
	 *            XML Document
	 * @param xpath
	 *            XPath Object
	 * @return String Hand ID number
	 */
	private static String getHandID(Document doc, XPath xpath) {
		String idNumber = null;
		try {
			XPathExpression expr = xpath.compile("G/@id");
			idNumber = (String) expr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return idNumber;
	}

	/**
	 * Creates the string for the header of the hand printout
	 * 
	 * @param hand
	 * @return hand header
	 */
	private static String getHeaderString(Hand hand) {
		String header = "";
		header = hand.getWebsite() + " Hand #" + hand.getID() + ": ";

		return header;
	}
}
