//
//  JBAnalytics.m
//  黄金象
//
//  Created by MAC005 on 2019/1/21.
//

#import "JBAnalytics.h"
#import "JBUserDefaults.h"
#import "JBDevice.h"
#import "ChannelPlugin.h"
#import "XYNetworking.h"

#define baseApiUrl @"https://app.goldrock.cn"
@implementation JBAnalytics

static NSString*_currentDateStr=nil;
+(NSString*)currentDateStr{
    if (_currentDateStr==nil) {
        _currentDateStr=@"";
    }
    
    return _currentDateStr;
}

+(void)setCurrentDateStr:(NSString *)currentDateStr{
    
    if (currentDateStr!=_currentDateStr) {
        _currentDateStr=currentDateStr;
    }
}

+ (void)initAnalytics
{
    JBDevice*device=[[JBDevice alloc]init];
    NSMutableDictionary*dic=[NSMutableDictionary dictionary];
    NSMutableDictionary*appdataValue=[NSMutableDictionary dictionaryWithDictionary:[device deviceProperties]];
    
    appdataValue[@"triggerTime"]=[self getDateStrFromDate:[NSDate date]];
    
    [self setCurrentDateStr:appdataValue[@"triggerTime"]];
    appdataValue[@"status"]=[NSNumber numberWithBool:NO];
    dic[@"appData"]=appdataValue;
    dic[@"appChannel"]=[self getChannelStr];
    dic[@"appVersion"]=[self getAppVersionStr];
    dic[@"eventId"]=@"launch_firstActivatedUser";
    dic[@"projectId"]=@"02";
    dic[@"source"]=@"APP";
    
    NSMutableArray*launchArray =[JBUserDefaults getFailedLaunchs];
    [launchArray addObject:dic];
    [JBUserDefaults setFailedLaunchs:launchArray];
}

+(void)reportErrorLaunches{
    NSMutableArray*array =[JBUserDefaults getFailedLaunchs];
 
    NSDictionary*dic =array.lastObject;
    
    if (!dic) {
        return;
    }
    
    if ([dic[@"appData"][@"triggerTime"] isEqualToString:[self currentDateStr]]) {
        return;
    }

    [XYNetworking postRequestByServiceUrl:baseApiUrl andApi:@"/metis/put/event" andParams:dic andResponseHeader:^(id obj) {
        
        if ([obj statusCode]==200) {
                [array removeObject:dic];
                [JBUserDefaults setFailedLaunchs:array];
        }
    } andCallBackBody:nil];
}

+(void)reportSuccessLaunches{
    NSMutableArray*array =[JBUserDefaults getFailedLaunchs];
    NSMutableDictionary*dic =array.lastObject;
    
    if (dic) {
        dic[@"appData"][@"status"]=[NSNumber numberWithBool:YES];
        
        [XYNetworking postRequestByServiceUrl:baseApiUrl andApi:@"/metis/put/event" andParams:dic andResponseHeader:^(id obj) {
            
            if ([obj statusCode]==200) {
                if ([dic[@"appData"][@"triggerTime"] isEqualToString:[self currentDateStr]]) {
                    [array removeObject:dic];
                    [JBUserDefaults setFailedLaunchs:array];
                }
            }
        } andCallBackBody:nil];
    }
}

+(NSString *)getDateStrFromDate:(NSDate *)date
{
    NSDateFormatter *dateFormatter=[[NSDateFormatter alloc]init];//创建一个日期格式化器
    dateFormatter.dateFormat=@"yyyy-MM-dd HH:mm:ss.SSS";//指定转date得日期格式化形式
    return  [dateFormatter stringFromDate:date];
}

+(NSString*)getChannelStr{
    
    //获取渠道号
    NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"Info" ofType:@"plist"];
    NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithContentsOfFile:bundlePath];
    NSString *channel = [dict objectForKey:@"ZHIKU_CHANNEL"];
    return channel;
}

+(NSString*)getAppVersionStr
{
    NSString* thisVersion =[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"];
    return thisVersion;
}

@end
