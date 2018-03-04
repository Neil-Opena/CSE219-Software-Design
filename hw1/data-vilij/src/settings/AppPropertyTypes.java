package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,
    SCREENSHOT_RESOURCE_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    INVALID_DATA_TITLE,
    INVALID_DATA_MESSAGE,
    NO_DATA_MESSAGE,
    IO_ERROR_TITLE,
    IO_SAVE_ERROR_MESSAGE,
    IO_LOAD_ERROR_MESSAGE,
    SAVE_ERROR_TITLE,
    SAVE_ERROR_MESSAGE,
    LOAD_ERROR_TITLE,
    LOAD_ERROR_MESSAGE,
    LARGE_DATA_TITLE,
    LARGE_DATA_MESSAGE_1,
    LARGE_DATA_MESSAGE_2,
    FILE_NOT_FOUND_TITLE,
    FILE_NOT_FOUND_MESSAGE,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,
    PNG_EXT,
    PNG_EXT_DESC,
    SCREENSHOT_TYPE,
    READ_ONLY,
    AVERAGE_Y,
    AVERAGE_Y_TOOLTIP,
    DECIMAL_FORMAT,

    /* layout user interface*/
    TITLE_STYLE,
    DISPLAY_BUTTON,
    CHART_TITLE,

    /* css */
    CSS_FILE,
    GRAY_TEXT,

}
