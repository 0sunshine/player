#include "rtc_ffm_api.h"
#include "avformat.h"
#include "avio_internal.h"
#include "libavutil/avassert.h"
#include "libavutil/parseutils.h"
#include "libavutil/fifo.h"
#include "libavutil/intreadwrite.h"
#include "libavutil/avstring.h"
#include "libavutil/opt.h"
#include "libavutil/log.h"
#include "libavutil/time.h"
#include "internal.h"
#include "network.h"
#include "os_support.h"
#include "url.h"

typedef struct AvitRtcContext {
    const AVClass *class;
    
} AvitRtcContext;

static const AVOption options[] = {
    { NULL }
};

static const AVClass avitrtc_class = {
    .class_name = "avitrtc",
    .item_name  = av_default_item_name,
    .option     = options,
    .version    = LIBAVUTIL_VERSION_INT,
};

static int avit_rtc_open(URLContext *h, const char *uri, int flags)
{
    int ret = avit_rtc_sdk_start(uri);

    if(ret != 0)
    {
        return AVERROR(EIO);
    }

    return 0;
}

static int avit_rtc_read(URLContext *h, uint8_t *buf, int size)
{
    int ret = 0;
    
    do
    {
        int already_wait_time = 0;
        
        ret = avit_rtc_sdk_read(buf, size);
        if(ret != 0)
        {
            break;
        }

        if(already_wait_time > 2 * 1000 * 1000) //2s
        {
            return AVERROR(EIO);
        }

        int sleep_time = 5 * 1000;
        already_wait_time += sleep_time;
        av_usleep(sleep_time);
    } while (1);
  
    return ret < 0 ? AVERROR(EIO) : ret;
}

static int avit_rtc_close(URLContext *h)
{
    int ret = avit_rtc_sdk_stop();

    return 0;
}

const URLProtocol ff_avit_rtc_protocol = {
    .name                = "avitrtc",
    .url_open            = avit_rtc_open,
    .url_read            = avit_rtc_read,
    .url_write           = NULL,
    .url_close           = avit_rtc_close,
    .url_get_file_handle = NULL,
    .priv_data_size      = sizeof(AvitRtcContext),
    .priv_data_class     = &avitrtc_class,
    .flags               = URL_PROTOCOL_FLAG_NETWORK,
};
