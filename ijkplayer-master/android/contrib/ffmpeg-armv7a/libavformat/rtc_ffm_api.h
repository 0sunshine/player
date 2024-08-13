#ifndef __RTC_FFM_API_H__
#define __RTC_FFM_API_H__

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

int avit_rtc_sdk_start(const char* url);
int avit_rtc_sdk_stop();
int avit_rtc_sdk_read(uint8_t* buffer, int32_t buffer_size);

#ifdef __cplusplus
}
#endif

#endif
