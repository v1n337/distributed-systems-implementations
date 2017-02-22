package ca.uwaterloo.java_fuse;


import lombok.Getter;
import lombok.ToString;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.logging.Logger;

@Getter
@ToString
public class Options
{
    private static Options instance;
    private AppConfig appConfig;

    private Logger log = Logger.getLogger(Options.class.getName());

    @Option(name = "-help", usage = "Help", metaVar = "HELP")
    private Boolean help = false;

    @Option(name = "-appConfigFile", usage = "App Config File", metaVar = "APP_CONFIG_FILE", required = true)
    private String appConfigFile;

    private Options(String[] args)
        throws Exception
    {
        CmdLineParser parser = new CmdLineParser(this);

        if (help)
        {
            parser.printUsage(System.out);
            System.exit(0);
        }

        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            String msg = "CmdLineException while reading options ";
            log.info(e.toString());
            throw new Exception(msg);
        }

        appConfig = Constants.MAPPER.readValue(new File(getAppConfigFile()), AppConfig.class);

        log.info("Options successfully read");
    }

    public static void initializeInstance(String[] args)
        throws Exception
    {
        if (null == instance)
        {
            instance = new Options(args);
        }
    }

    public static Options getInstance()
        throws Exception
    {
        if (null == instance)
        {
            throw new Exception("Tried accessing options without initializing it first.");
        }
        return instance;
    }
}
