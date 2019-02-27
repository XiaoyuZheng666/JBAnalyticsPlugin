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
// #define baseApiUrl @"http://testmetis.goldrock.cn:2752"
//#define baseApiUrl @"http://192.168.30.9:2752"

#define maxReportNum 90//同一个事件最多纪录90条
@implementation JBAnalytics

static NSString*_currentDateStr=nil;
static NSDictionary*_currentAppdata=nil;
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

+(NSDictionary*)currentAppdata{
    if (_currentAppdata==nil) {
        _currentAppdata=[NSDictionary dictionary];
    }
    
    return _currentAppdata;
}

+(void)setCurrentAppData:(NSDictionary *)currentAppdata{
    
    if (_currentAppdata!=currentAppdata) {
        _currentAppdata=currentAppdata;
    }
}

+ (void)initAnalytics
{
    JBDevice*device=[[JBDevice alloc]init];
    NSMutableDictionary*dic=[NSMutableDictionary dictionary];
    
    NSMutableDictionary*appdataValue=[NSMutableDictionary dictionaryWithDictionary:[device deviceProperties]];
    appdataValue[@"triggerTime"]=[self getDateStrFromDate:[NSDate date]];
    [self setCurrentDateStr:appdataValue[@"triggerTime"]];
    appdataValue[@"status"]=[NSNumber numberWithBool:YES];
    
    [self setCurrentAppData:[appdataValue copy]];
    
    appdataValue[@"status"]=[NSNumber numberWithBool:NO];
    dic[@"appData"]=appdataValue;
    dic[@"appChannel"]=[self getChannelStr];
    dic[@"appVersion"]=[self getAppVersionStr];
    dic[@"eventId"]=@"launch_firstActivatedUser";
    dic[@"projectId"]=@"02";
    dic[@"source"]=@"APP";
    
    NSMutableArray*launchArray =[JBUserDefaults getLaunchs];
    [launchArray addObject:dic];
    [JBUserDefaults setLaunchs:launchArray];
}

+(void)reportSavedEvents{
    
    [self reportSavedLaunchEvents];
    [self reportSavedOtherEvents];
}

+(void)reportSavedLaunchEvents{
    //处理启动事件
    NSMutableArray*reportArray=[NSMutableArray array];
    
    NSMutableArray*array =[JBUserDefaults getLaunchs];
    
    if (array==nil||array.count==0) {
        return;
    }
    
    for (NSDictionary*dic in array) {
        
        if ([dic[@"appData"][@"triggerTime"] isEqualToString:[self currentDateStr]]) {
            continue;
        }else{
            [reportArray addObject:dic];
        }
    }
    
    if (reportArray.count==0) {
        return;
    }
    [XYNetworking postRequestByServiceUrl:baseApiUrl andApi:@"/metis/put/event" andParams:reportArray andResponseHeader:^(id obj) {
        
        if ([obj statusCode]==200) {
            [array removeObjectsInArray:reportArray];
            [JBUserDefaults setLaunchs:array];
        }
    } andCallBackBody:nil];
}

+(void)reportSavedOtherEvents{
    //处理前端过来的保存的发送失败的事件
    NSMutableArray*reportTempArray=[NSMutableArray array];
    NSMutableArray*eventidsTempArray=[NSMutableArray array];

    NSMutableArray*array =[JBUserDefaults getEventIds];
    
    for (NSString*eventid in array) {
        NSMutableArray*records =[JBUserDefaults getRecordsWithKey:eventid];
        [reportTempArray addObjectsFromArray:records];
        [eventidsTempArray addObject:eventid];
        
        if (reportTempArray.count>99) {
            [reportTempArray removeObjectsInArray:records];
            [eventidsTempArray removeObject:eventid];
            break;
        }
    }
    
    if (reportTempArray.count==0) {
        return;
    }
    [XYNetworking postRequestByServiceUrl:baseApiUrl andApi:@"/metis/put/eventBatch" andParams:reportTempArray andResponseHeader:^(id obj) {
        
        if ([obj statusCode]==200) {
            for (NSString*eventid in eventidsTempArray) {
                NSMutableArray*records =[JBUserDefaults getRecordsWithKey:eventid];
                [records removeAllObjects];
                [JBUserDefaults setRecords:records withKey:eventid];
            }
            [array removeObjectsInArray:eventidsTempArray];
            [JBUserDefaults setEventIds:array];
        }
    } andCallBackBody:nil];
}

+(void)reportSucceededLaunch{
    NSMutableArray*array =[JBUserDefaults getLaunchs];

        if (array==nil||array.count==0) {
        return;
    }
    
    NSMutableDictionary*dic =array.lastObject;
    
    if (dic) {
        
        //是这次触发的才算成功启动
        if (![dic[@"appData"][@"triggerTime"] isEqualToString:[self currentDateStr]]) {
            return;
        }

        dic[@"appData"][@"status"]=[NSNumber numberWithBool:YES];
        dic[@"appData"][@"triggerTime"]=[self getDateStrFromDate:[NSDate date]];

        
        [XYNetworking postRequestByServiceUrl:baseApiUrl andApi:@"/metis/put/event" andParams:dic andResponseHeader:^(id obj) {
            
            if ([obj statusCode]==200) {
                
                    [array removeObject:dic];
            }
            [JBUserDefaults setLaunchs:array];


        } andCallBackBody:nil];
    }
}

+(void)reportSucceededEventwithId:(NSString*)eventId andDataDic:(NSDictionary*)dataDic{
    
    if (dataDic==nil) {
        return;
    }
    
    NSMutableDictionary*totalDic=[NSMutableDictionary dictionaryWithDictionary:dataDic];
    
    NSMutableDictionary*mutableAppdata=[NSMutableDictionary dictionaryWithDictionary:[self currentAppdata]];
    mutableAppdata[@"triggerTime"]=[self getDateStrFromDate:[NSDate date]];
    totalDic[@"appData"]=mutableAppdata;
    
    [XYNetworking postRequestByServiceUrl:baseApiUrl andApi:@"/metis/put/event" andParams:totalDic andResponseHeader:^(id obj) {
        
        if ([obj statusCode]!=200) {
        
            NSMutableArray*eventids =[JBUserDefaults getEventIds];
            
            if (![self containsStr:eventId inArray:eventids]) {
                [eventids addObject:eventId];
            }
            
            [JBUserDefaults setEventIds:eventids];
            
            NSMutableArray*records=[JBUserDefaults getRecordsWithKey:eventId];
            
            if(records.count>maxReportNum){
                [records removeObject:records.firstObject];
            }
            
            [records addObject:totalDic];
            [JBUserDefaults setRecords:records withKey:eventId];
        }
    } andCallBackBody:nil];
}

//判断某个字符串是否在数组内部
+(BOOL)containsStr:(NSString*)str inArray:(NSArray*)array{
    
    for (NSString*string in array) {
        if ([string isEqualToString:str]) {
            return YES;
        }
    }
    return NO;
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
