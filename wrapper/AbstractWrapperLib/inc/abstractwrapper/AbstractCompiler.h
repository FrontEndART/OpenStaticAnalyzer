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

#ifndef __ABSTRACT_COMPILER_H
#define __ABSTRACT_COMPILER_H

#include <string>
#include <set>
#include "AbstractWrapper.h"
#include "AbstractWrapperLib/inc/paramsup/ParamsupCommon.h"

#if defined _WIN64
#define CPPCHECK_PLATFORM "win64"
#elif defined _WIN32
#define CPPCHECK_PLATFORM "win32A"
#elif defined __GNUC__
#if __x86_64__ || __ppc64__
#define CPPCHECK_PLATFORM "unix64"
#else
#define CPPCHECK_PLATFORM "unix32"
#endif
#endif

namespace ColumbusWrappers {

  class AbstractCompiler : virtual public AbstractWrapper {
  public:
    /**
     * @brief Constructor of AbstractCompiler class.
     * @param configfile     [in] The name of the config file.
     */
    AbstractCompiler(std::string configfile);

    /**
     * @brief Collects the arguments for CAN and calls CAN with this command line.
     * @param compArgs           [in] The arguments of CAN (which are according to known compiler arguments) are in this struct.
     * @param preprocArgs        [in] The arguments of CAN (which are according to known preprocessor arguments) are in this struct.
     * @param generated_files    [out] Argument list for files generated by CAN.
     * @return                         True, if there is no problem with execution.
     */
    bool executeCompiler(CompilerArgs& compArgs, PreprocArgs& preprocArgs, std::list<Argument>& generated_files) const;

    /**
     * @brief Collects the arguments for preprocessing, instrumenter calling and compiling with preprocessed, instrumented files, and calls these commands.
     * @param inputArgs          [in] The original input arguments.
     * @param compArgs           [in] The arguments of CAN (which are according to known compiler arguments) are in this struct.
     * @param linkArgs           [in] The arguments of CANLink (which are according to known linker arguments) are in this struct.
     * @return                        True, if there is no problem with executions.
     */
    bool executeInstrumenter(std::list<std::string> inputArgs, CompilerArgs& compArgs, LinkerArgs& linkArgs) const;

  protected:
    /**
     * @brief Reads options from config file.
     */
    void readConfig();

    /**
     * @brief Processes input list. Put prefix before the elements of the list and concatenate them to a string.
     * @param prefix               [in] The given prefix.
     * @param input_list           [in] Argument list for input arguments.
     * @param output_paramlist     [out] Argument list for CAN commandline.
     * @param separately           [in] True, if space is needed between prefix and parameter.
     */
    void concatenatePrefixAndParams(const char* prefix, const std::list<Argument>& input_list, std::list<Argument>& output_paramlist, bool separately = true) const;

    int comp_needtorun;                                             ///< compiling needed or not from config
    int instrument_mode;                                            ///< define instrumentatation mode
    int comp_needstat;                                              ///< CAN needed to create stat file or not
    int run_cppcheck;                                               ///< cppcheck need to run or not
    int comp_ml;                                                    ///< CAN message level
    std::set<std::pair<std::string, int> > comp_paramtoskip;        ///< arguments and their number of parameters to skip
    int comp_numofparamtoskip;                                      ///< number of arguments to skip
    std::vector<std::string> comp_extraparam;                       ///< extra parameters for CAN
    int comp_numofextraparam;                                       ///< number of extra parameters

    std::string comp_tool;                                          ///< name of the compiler tool (CAN)
  };
}

#endif