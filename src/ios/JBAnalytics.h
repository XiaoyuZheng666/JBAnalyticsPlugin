//
//  JBAnalytics.h
//  黄金象
//
//  Created by MAC005 on 2019/1/21.
//

#import <Foundation/Foundation.h>
#define kJBLaunchSuccessNotification @"JBLaunchSuccessNotification"

@interface JBAnalytics : NSObject

@property(class,nonatomic,copy) NSString*currentDateStr;

+ (void)initAnalyticsWithBaseApiUrl:(NSString*)url;

+(void)reportSavedEvents;

//发送这次成功的启动
+(void)reportSucceededLaunch;

+(void)reportSucceededEventwithId:(NSString*)eventId andDataDic:(NSDictionary*)dataDic;


@end

