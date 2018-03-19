/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "dictionary.h"
#include "utils.h"

namespace hipo {

  int   schema::getType( const char* entry){
    if(schemaEntries.count(entry)==0){
      printf("schema:: error, schema %s does no contain entry %s\n",
          schemaName.c_str(), entry);
    }
    return schemaEntries[entry].second;
  }

  int   schema::getItem(  const char* entry){
    if(schemaEntries.count(entry)==0){
      printf("schema:: error, schema %s does no contain entry %s\n",
          schemaName.c_str(), entry);
    }
    return schemaEntries[entry].first;
  }

  std::string schema::getTypeString(int type){
    std::string typeString = "<unknown>";
    switch(type){
      case 1: typeString = "<uint8_t>"  ; break;
      case 2: typeString = "<uint16_t>" ; break;
      case 3: typeString = "<uint32_t>" ; break;
      case 4: typeString = "<float>"    ; break;
      case 5: typeString = "<double>"   ; break;
      case 8: typeString = "<uint64_t>" ; break;
      default: break;
    }
    return typeString;
  }

  std::vector<std::string> schema::branchesCode(){
    std::vector<std::string> code;
    std::string scname = getName();
    for (std::map<std::string, std::pair<int,int> >::iterator it=schemaEntries.begin();
         it!=schemaEntries.end(); ++it){
           std::string type = getTypeString(it->second.first);
           std::string node("\tnode");
           node.append(type);
           node.append("  *");
           node.append(scname);
           node.append("_");
           node.append(it->first);
           node.append("  =  reader.getBranch(\"");
           node.append(scname);
           node.append("\",\"");
           node.append(it->first);
           node.append("\");");
           code.push_back(node);
           //std::cout << it->first << " => " << it->second << '\n';
    }
    return code;
  }

  void dictionary::parse(std::string dictString){
    std::vector<std::string> tokens;
    std::string schemahead = hipo::utils::substring(dictString,"{","}",0);
     hipo::utils::tokenize(schemahead, tokens, ",");
     hipo::schema  schema(tokens[0].c_str());
     int group = std::stoi(tokens[1]);
     schema.setGroup(group);
     printf("schema found %s  %d\n", tokens[0].c_str(),group);
     bool status = true;
     int counter = 0;
     int order   = 0;
     while(status){
       std::string item = hipo::utils::substring(dictString, "[","]",order);
       order++;
       if(item.size()<2){
         status = false;
       } else {
         tokens.clear();
         hipo::utils::tokenize(item, tokens, ",");
         printf("\t found item %s %s %s\n",tokens[0].c_str(),tokens[1].c_str(),
                tokens[2].c_str());
       }
     }
  }


}
