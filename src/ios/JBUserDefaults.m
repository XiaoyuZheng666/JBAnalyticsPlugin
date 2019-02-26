//
//  JBUserDefaults.m
//  黄金象
//
//  Created by MAC005 on 2019/1/18.
//

#import "JBUserDefaults.h"
#import "SBJson.h"

#define kUserDefault_Launch @"UserDefault_Launch"
#define kUserDefault_Event @"UserDefault_Event"


@implementation JBUserDefaults

+(NSMutableArray *)getLaunchs{
    NSString *json = [self getSeting:kUserDefault_Launch];
    NSArray *datas = [json JSONValue];
    json = nil;
    return [NSMutableArray arrayWithArray:datas];
}

+(void)setLaunchs:(NSMutableArray *)datas{
    if (datas) {
        NSString *json = [datas JSONRepresentation];
        [self setSeting:kUserDefault_Launch Value:json];
        json = nil;
    }

}

+(NSMutableArray *)getEventIds{
    NSString *json = [self getSeting:kUserDefault_Event];
    NSArray *datas = [json JSONValue];
    json = nil;
    return [NSMutableArray arrayWithArray:datas];
}

+(void)setEventIds:(NSMutableArray *)datas{
    if (datas) {
        NSString *json = [datas JSONRepresentation];
        [self setSeting:kUserDefault_Event Value:json];
        json = nil;
    }
}

+(NSMutableArray*)getRecordsWithKey:(NSString*)key{
    NSString *json = [self getSeting:key];
    NSArray *datas = [json JSONValue];
    json = nil;
    return [NSMutableArray arrayWithArray:datas];
}

+(void)setRecords:(NSMutableArray*)datas withKey:(NSString*)key{
    if (datas) {
        NSString *json = [datas JSONRepresentation];
        [self setSeting:key Value:json];
        json = nil;
    }
}


+(NSString *)getSeting:(NSString*)key{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *value=[defaults objectForKey:key];
    if (!value) {
        value = @"";
    }
    defaults = nil;
    return value;
}

+(void)setSeting:(NSString *)key Value:(NSString*)value{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:value forKey:key];
    [defaults synchronize];
    defaults = nil;
}


@end
