

import java.awt.Toolkit;




    public interface Applicationconstants {


        public final static String JAR_NAME = "fdzFunktionssimulator.jar";



        public final static String CMD_YES = "Yes";

        public final static String CMD_NO = "No";

        public final static String CMD_OK = "OK";


        public final static String CMD_ABORT = "Cancel";

        public final static String CMD_RESET = "Reset";

        public final static String CMD_NEXT = "Next";


        public final static String CMD_JSAP = "JSAP";

        public final static String CMD_CONTROL = "Control";

        public final static String CMD_STORAGECONTROL = "Storage-Control";

        public final static String CMD_ROBOTCONTOL = "Robot-Control";

        public final static String CMD_TRANSPORTCONTOL = "Transport-Control";

        public final static String CMD_IOCONTOL = "IO-Control";

        public final static String CMD_STORAGE = "Storage";

        public final static String CMD_ROBOT = "Robot";

        public final static String CMD_TRANSPORT = "Transport";

        public final static String CMD_IO = "IO-Station";



        public final static String DEFAULT_PORT_CO = "40000";

        public final static String DEFAULT_PORT_STCO = "40002";

        public final static String DEFAULT_PORT_ROCO = "40003";

        public final static String DEFAULT_PORT_TRCO = "40001";

        public final static String DEFAULT_PORT_IOCO = "40004";

        public final static String DEFAULT_PORT_ST = "30000";

        public final static String DEFAULT_PORT_RO = "30000";

        public final static String DEFAULT_PORT_TR = "30000";

        public final static String DEFAULT_PORT_IO = "30000";



        public final static String DEFAULT_IP = "127.0.0.1";

        public final static String DEFAULT_PORT = "30000";



        public final static String CMD_CONNECT = "Connect";

        public final static String CMD_DISCONNECT = "Disconnect";


        public final static String CMD_NUMBERS = "numbers";

        public final static String CMD_RGYBW = "rgybw";

        public final static String CMD_RGYBW_ = "rgybw-";

        public final static String CMD_RGYBW_X = "rgybwX-";

        public final static String CMD_SLEIGHPOS = "sleighpos";



        public final static String DEBUG = "debug";

//	public final static String DEBUG_PATH = "Java/src/";

        public final static String DEBUG_PATH = "";



        public final static String ICON_ROBOT = "fdzFunktionssimulator/presentationLayer/images/robot.jpg";

        public final static String ICON_STORAGE = "fdzFunktionssimulator/presentationLayer/images/storage.jpg";

        public final static String ICON_TRANSPORT = "fdzFunktionssimulator/presentationLayer/images/transport.jpg";

        public final static String ICON_IO = "fdzFunktionssimulator/presentationLayer/images/io.jpg";

        public final static String ICON_CONTROL = "fdzFunktionssimulator/presentationLayer/images/control.jpg";

        public final static String ICON_ICON = "fdzFunktionssimulator/presentationLayer/images/icon.png";

        public final static String ICON_INFO = "fdzFunktionssimulator/presentationLayer/images/info.png";

        public final static String ICON_RIGHT = "fdzFunktionssimulator/presentationLayer/images/right.gif";



        public final static String OUT_WAITING_CON = "Waiting for connection ...";

        public final static String OUT_WAITING = "Waiting for message ...";

        public final static String OUT_CONNECTED = "Connected to ";

        public final static String OUT_RECEIVED = "Received: ";

        public final static String OUT_SENT = "Sent: ";

        public final static String OUT_ERROR = "Error, wrong message: ";

        public final static String OUT_READY = "Ready to send answer ";

        public final static String OUT_LOST = "Lost connection";

        public final static String OUT_ABORT = " Abort";

        public final static String OUT_TRY = "Try to connect to ";

        public final static String OUT_DATA_CHANGED = "Connection data changed to ";

        public final static String OUT_ERROR_UNKNOWN = "Unknown instruction: ";

        public final static String OUT_MANUALLY_DISCONNECTED = "Connection closed";


        public final static String XML_TYPE_SUCCESS = "success";

        public final static String XML_TYPE_ERROR = "error";


        public final static String FILE_MESSAGE_STREAM = "fsMessage.log";


        public final static String STR_NEW_LINE  = "_";

        public final static String STR_PARAM  = "~";

        public final static String STR_RO  = "ro";

        public final static String STR_LA  = "la";

        public final static String STR_EA  = "ea";





        /**

         * path for connection file, where ip-address and port are saved

         */

        public final static String FILE_CONNECTION = "fs_connection.prp";

        public final static String FILE_JSAP_XML = "fdzFunktionssimulator/businessLayer/xml/JSAP.xml";

        public final static String FILE_STORAGE_XML = "fdzFunktionssimulator/businessLayer/xml/Lager.xml";

        public final static String FILE_ROBOT_XML = "fdzFunktionssimulator/businessLayer/xml/Robot.xml";

        public final static String FILE_TRANSPORT_XML = "fdzFunktionssimulator/businessLayer/xml/Transport.xml";

        public final static String FILE_IO_XML = "fdzFunktionssimulator/businessLayer/xml/IO.xml";



        public final static String FILE_JSAP_XML_EXTERN = "extern/JSAP.xml";

        public final static String FILE_STORAGE_XML_EXTERN = "extern/Lager.xml";

        public final static String FILE_ROBOT_XML_EXTERN = "extern/Robot.xml";

        public final static String FILE_TRANSPORT_XML_EXTERN = "extern/Transport.xml";

        public final static String FILE_IO_XML_EXTERN = "extern/IO.xml";



        //public final static ErrorDialog errorDialog = new ErrorDialog(Main.getMainFrame(), true);



        //public final static JarExtractor jarExtractor = new JarExtractor(JAR_NAME);


        public final static Toolkit defaultToolkit = Toolkit.getDefaultToolkit();

    }

