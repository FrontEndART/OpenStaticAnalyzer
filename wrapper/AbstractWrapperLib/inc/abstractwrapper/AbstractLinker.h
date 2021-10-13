/*
 *  This file is part of OpenStaticAnalyzer.
 *
 *  Copyright (c) 2004-2018 Department of Software Engineering - University of Szeged
 *
 *  Licensed under Version 1.2 of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *
 *  You may obtain a copy of the Licence in the LICENSE file or at:
 *
 *  https://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 */

#ifndef __ABSTRACT_LINKER_H
#define __ABSTRACT_LINKER_H

#include <string>
#include <set>
#include "AbstractWrapper.h"
#include "../paramsup/ParamsupCommon.h"

namespace ColumbusWrappers {
  class AbstractLinker : virtual public AbstractWrapper {

  public:
    /**
     * @brief Constructor of AbstractLinker class.
     * @param configfile     [in] The name of the config file.
     */
    AbstractLinker(std::string configfile);

    /**
     * @brief Collects the arguments for CANLink and calls it.
     * @param linkerArgs           [in] The arguments of CANLink (which are according to known linker arguments) are in this struct.
     * @param cmp_generated_files  [in] Argument list for files generated by CAN.
     * @return                          True, if there is no problem with execution.
     */
    bool executeLinker (LinkerArgs& linkerArgs, std::list<Argument>& cmp_generated_files) const;

  protected:
    /**
     * @brief Reads options from config file.
     */
    void readConfig();

    /**
     * @brief Examines linker_lib_files input list using linker_lib_paths list and collect existing lib files.
     * @param linkerArgs           [in] The arguments of CANLink (which are according to known linker arguments) are in this struct.
     * @param libFiles             [out] Argument list for existing lib files.
     */
    void examineLibs(LinkerArgs& linkerArgs, std::list<Argument>& libFiles) const;

    /**
     * @brief Creates from input file list a string which contains input file names separated by '\n' and a string for command. Put ".ast" or ".aast" extension to the end of files.
     * @param i_files              [in] Argument list for input files.
     * @param output_paramlist     [out] Argument list for CANLink commandline.
     * @return                         True if there is at least on file for linker.
     */
    bool createInputNames(const std::list<Argument>& i_files, std::list<Argument>& output_paramlist) const;

    /**
     * @brief Creates from CAN output file list a string which contains input file names separated by '\n' and a string for command.
     * @param i_files              [in] Argument list for input files.
     * @param output_paramlist     [out] Argument list for CANLink commandline.
     * @return                          True if there is at least on file for linker.
     */
    bool createInputNamesFromCANOutput(const std::list<Argument>& i_files, std::list<Argument>& output_paramlist) const;

    int linker_needtorun;                                                 ///< linking needed or not from config
    std::string linker_filterfile;                                        ///< name of linker filter file
    int linker_needstat;                                                  ///< CANLink needed to create stat file or not
    int linker_ml;                                                        ///< CANLink message level
    std::set<std::pair<std::string, int> > linker_paramtoskip;            ///< arguments and their number of parameters to skip
    int linker_numofparamtoskip;                                          ///< number of arguments to skip
    std::vector<std::string> linker_extraparam;                           ///< extra parameters for CANLink
    int linker_numofextraparam;                                           ///< number of extra parameters
    int linking_mode;                                                     ///< linking mode
    
    std::string link_tool;                                                ///< name of the compiler tool (CANLink)
  };
}

#endif
